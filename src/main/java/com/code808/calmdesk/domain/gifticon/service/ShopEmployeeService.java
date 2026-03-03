package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.Notification.event.NotificationEvent;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.event.GifticonUpdateEvent;
import com.code808.calmdesk.domain.gifticon.dto.*;
import com.code808.calmdesk.domain.gifticon.entity.*;
import com.code808.calmdesk.domain.member.entity.Account;
import com.code808.calmdesk.domain.gifticon.repository.*;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.AccountRepository;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List; // 추가 필수
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopEmployeeService {

    private final GifticonRepository gifticonRepository;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    private final MemberMissionRepository memberMissionRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final CompanyGifticonRepository companyGifticonRepository;

    // ⭐ 이벤트 발행을 위한 주입
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // 💡 수정: itemId가 이제 CompanyGifticon의 PK이므로 findById로 조회합니다.
        CompanyGifticon companyGifticon = companyGifticonRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("상품 설정을 찾을 수 없습니다."));

        // 💡 보안 검증: 현재 구매하려는 사용자의 회사와 상품의 소유 회사가 일치하는지 확인
        if (!companyGifticon.getCompany().getCompanyId().equals(member.getCompany().getCompanyId())) {
            throw new RuntimeException("해당 회사에서 판매하지 않는 상품입니다.");
        }

        // 활성화 여부 및 재고 체크 (기존 유지)
        if (!companyGifticon.getIsActive()) {
            throw new RuntimeException("현재 관리자에 의해 판매가 중지된 상품입니다.");
        }

        if (companyGifticon.getStockQuantity() <= 0) {
            throw new RuntimeException("상품 재고가 없습니다.");
        }

        // 계좌 조회 및 포인트 차감
        Account account = accountRepository.findByMember_MemberId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("계좌 정보를 찾을 수 없습니다."));

        int price = request.getPrice().intValue();
        account.withdraw(price);

        // 회사 전용 재고 감소
        companyGifticon.setStockQuantity(companyGifticon.getStockQuantity() - 1);

        // 주문(Order) 저장
        Order order = Order.builder()
                .member(member)
                .gifticon(companyGifticon.getGifticon()) // 마스터 정보 연결
                .orderDate(LocalDate.now())
                .approvalAmount(price)
                .spendPoint(price)
                .earnPoint(0)
                .type(Order.Type.SPEND)
                .period(30)
                .build();

        orderRepository.save(order);

        // 포인트 히스토리 저장
        PointHistory history = new PointHistory(
                "SPEND", (long) price, (long) account.getAccountLeave(),
                "GIFTICON", member, companyGifticon.getGifticon(), null);
        pointHistoryRepository.save(history);

        eventPublisher.publishEvent(new NotificationEvent(
                member.getMemberId(),
                "상품 구매 완료",
                "'" + companyGifticon.getGifticon().getGifticonName() + "' 구매가 완료되었습니다. 마이페이지에서 확인하세요!",
                "USER",
                "/app/mypage/coupons"));

        // 3. ⭐ 해당 회사의 관리자(ADMIN)를 찾아 알림 발행
        // MemberRepository에 findByCompany_CompanyIdAndRole(companyId, role) 같은 메서드가 있다고
        // 가정합니다.
        Long companyId = member.getCompany().getCompanyId();
        // DB 저장값이 "ADMIN"이므로 문자열로 넘겨줍니다.
        // 만약 Role이 Enum이라면 MemberRole.ADMIN을 넘겨주세요.
        List<Member> admins = memberRepository.findAllByCompany_CompanyIdAndRole(companyId, Member.Role.ADMIN);

        admins.forEach(admin -> {
            eventPublisher.publishEvent(new NotificationEvent(
                    admin.getMemberId(),
                    "신규 구매",
                    member.getName() + "님이 구매했습니다.",
                    "ADMIN",
                    null));
        });

        // ✅ 실시간 업데이트 이벤트 발행 (관리자 및 다른 직원들의 화면을 위해)
        eventPublisher.publishEvent(new GifticonUpdateEvent(companyId));

        return order.getOrderId();
    }

    @Transactional(readOnly = true)
    public PointMallResponse getPointMallData(Long userId, Long companyId) { // userId 타입을 String으로 통일 권장
        // 1. 포인트 조회
        Account account = accountRepository.findByMember_MemberId(userId)
                .orElseThrow(() -> new RuntimeException("계좌 정보 없음"));

        List<CompanyGifticon> companyItems = companyGifticonRepository
                .findAllByCompany_CompanyIdAndIsActiveTrue(companyId);

        // 2. ⭐ 수정: 해당 회사의 기프티콘만 조회
        List<ItemResponse> items = companyItems.stream()
                .map(ItemResponse::fromCompanyEntity) // 👈 builder 대신 이 메서드 사용!
                .collect(Collectors.toList());

        // 3. ⭐ 중요: 사용자별 미션 상태(Y/N)가 반영된 리스트를 가져오도록 수정
        // 단순히 missionRepository.findAll()을 하는 대신, 아래 구현한 메서드를 호출합니다.
        List<MissionResponse> missions = getAllMissions(userId);

        return PointMallResponse.builder()
                .currentPoint(account.getAccountLeave())
                .shopItems(items)
                .missions(missions) // 이제 상태가 포함된 리스트가 반환됨
                .build();
    }

    @Transactional(readOnly = true)
    public List<MissionResponse> getAllMissions(Long userId) {
        List<MissionList> allMissions = missionRepository.findByStatus(CommonEnums.Status.Y);
        List<MemberMission> userMissions = memberMissionRepository.findByMember_MemberId(userId);

        return allMissions.stream().map(mission -> {
            // 해당 미션에 대한 유저의 기록 찾기
            MemberMission userRecord = userMissions.stream()
                    .filter(um -> um.getMissionList().getMissionListId()
                    .equals(mission.getMissionListId()))
                    .findFirst()
                    .orElse(null);

            return MissionResponse.builder()
                    .id(mission.getMissionListId())
                    .title(mission.getRewardName())
                    .description(mission.getRewardDescription())
                    .reward(mission.getPointAccount())
                    // ⭐ 추가: 진행률 정보 전달 (리액트 ProgressBar용)
                    .progressCount(userRecord != null ? userRecord.getProgressCount() : 0)
                    .goalCount(mission.getGoalCount())
                    .status(userRecord != null ? userRecord.getStatus().name() : "N")
                    .build();
        }).collect(Collectors.toList());
    }

    // 2. 보상 수령 로직 (목표 달성 체크 추가)
    @Transactional
    public void completeMission(Long memberId, Long missionId) {

        // 1. 미션 수행 이력 조회 (없으면 새로 생성하여 가져옴)
        MemberMission memberMission = memberMissionRepository
                .findByMember_MemberIdAndMissionList_MissionListId(memberId, missionId)
                .orElseGet(() -> {
                    // 미션 이력이 없는 경우, Member와 Mission 정보를 찾아 새로 생성
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new EntityNotFoundException(
                            "해당 회원을 찾을 수 없습니다. ID: " + memberId));

                    MissionList mission = missionRepository.findById(missionId)
                            .orElseThrow(() -> new EntityNotFoundException(
                            "해당 미션을 찾을 수 없습니다. ID: " + missionId));

                    return MemberMission.builder()
                            .member(member)
                            .missionList(mission)
                            .progressCount(0)
                            .status(CommonEnums.Status.N)
                            .build();
                });

        // 2. 검증: 이미 완료된 미션인지 확인
        if (memberMission.getStatus() == CommonEnums.Status.Y) {
            throw new IllegalStateException("이미 보상을 획득한 미션입니다.");
        }

        // 3. 검증: 목표 수치에 도달했는지 확인
        if (memberMission.getProgressCount() < memberMission.getMissionList().getGoalCount()) {
            throw new IllegalStateException("미션 목표를 아직 달성하지 못했습니다. (현재: "
                    + memberMission.getProgressCount() + ", 목표: "
                    + memberMission.getMissionList().getGoalCount() + ")");
        }

        // 4. 계좌 정보 조회
        Account account = accountRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원의 계좌 정보가 없습니다."));

        // 5. 보상 지급 및 상태 변경
        int rewardPoint = memberMission.getMissionList().getPointAccount();
        account.deposit(rewardPoint); // 계좌에 포인트 입금
        memberMission.complete(); // status = 'Y' 처리 및 수정일 갱신

        // 6. DB 반영
        memberMissionRepository.save(memberMission);

        // 7. 실시간 알림 이벤트 발행
        // 이 코드가 실행되면 NotificationEventListener가 동작하여 DB 저장과 SSE 전송을 처리합니다.
        eventPublisher.publishEvent(new NotificationEvent(
                memberId,
                "미션 완료 및 보상 지급",
                "'" + memberMission.getMissionList().getRewardName() + "' 미션을 완료하여 "
                + rewardPoint + "포인트가 지급되었습니다!",
                "USER",
                null
        ));
    }

    // 3. 진행률 업데이트 (isAccumulative 파라미터 활용 최적화)
    @Transactional
    public void updateMissionProgress(Long memberId, String missionCode, int value, boolean isAccumulative) {
        MissionList mission = missionRepository.findByMissionCode(missionCode)
                .orElseThrow(() -> new RuntimeException("미션 코드 오류: " + missionCode));

        MemberMission memberMission = memberMissionRepository
                .findByMember_MemberIdAndMissionList_MissionListId(memberId, mission.getMissionListId())
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new RuntimeException("사용자 없음"));
                    return MemberMission.builder()
                            .member(member)
                            .missionList(mission)
                            .progressCount(0)
                            .status(CommonEnums.Status.N)
                            .build();
                });

        if (memberMission.getStatus() == CommonEnums.Status.Y) {
            return;
        }

        // ⭐ 엔티티의 updateProgress 활용
        memberMission.updateProgress(value, isAccumulative);

        // 목표치 초과 방지
        if (memberMission.getProgressCount() > mission.getGoalCount()) {
            memberMission.updateProgress(mission.getGoalCount(), false);
        }

        memberMissionRepository.save(memberMission);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseHistoryResponse> getAllPurchaseHistory(Long companyId, Pageable pageable) {
        // 1. 해당 회사의 기프티콘 구매 내역을 페이징하여 조회
        // (정렬은 컨트롤러에서 넘어온 pageable 설정을 따르거나 Repository 메서드명에서 처리 가능합니다)
        Page<PointHistory> historyPage = pointHistoryRepository
                .findByMember_Company_CompanyIdAndSourceType(companyId, "GIFTICON", pageable);

        // 2. Entity -> DTO 변환 (Page 객체 내부의 데이터를 변환)
        return historyPage.map(history -> PurchaseHistoryResponse.builder()
                .id(history.getId())
                .userName(history.getMember().getName())
                .itemName(history.getGifticon().getGifticonName())
                .itemPrice(history.getAmount().intValue())
                .itemImg(history.getGifticon().getImage())
                .purchaseDate(history.getCreateDate())
                .build());
    }
}
