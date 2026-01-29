package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.dto.*;
import com.code808.calmdesk.domain.gifticon.entity.*;
import com.code808.calmdesk.domain.member.entity.Account;
import com.code808.calmdesk.domain.gifticon.repository.*;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        // ... (기존 purchase 로직과 동일)
        Gifticon gifticon = gifticonRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (gifticon.getStockQuantity() <= 0) {
            throw new RuntimeException("상품 재고가 없습니다.");
        }

        // findByMember_MemberId의 매개변수 타입을 확인하세요 (String vs Long)
        Account account = accountRepository.findByMember_MemberId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("계좌 정보를 찾을 수 없습니다."));

//        account.withdraw(request.getPrice().intValue());

        int price = request.getPrice().intValue();
        account.withdraw(price); // 여기서 포인트가 차감됨
        gifticon.setStockQuantity(gifticon.getStockQuantity() - 1); // 재고 감소

        Order order = Order.builder()
                .member(account.getMember())
                .gifticon(gifticon)
                .orderDate(LocalDate.now())
                .approvalAmount(request.getPrice().intValue())
                .spendPoint(request.getPrice().intValue())
                .earnPoint(0)
                .type(Order.Type.SPEND)
                .period(1)
                .build();

        orderRepository.save(order).getOrderId();

        PointHistory history = new PointHistory(
                "SPEND",                       // pointType
                (long) price,                  // amount
                (long) account.getAccountLeave(),     // balanceAfter (차감 후 잔액)
                "GIFTICON",                    // sourceType
                request.getUserId(),           // memberId
                gifticon.getGifticonId(),      // gifticonId
                null                           // missionId (구매이므로 미션ID는 null)
        );
        pointHistoryRepository.save(history);
        return order.getOrderId();

    }

    @Transactional(readOnly = true)
    public PointMallResponse getPointMallData(Long userId) { // userId 타입을 String으로 통일 권장
        // 1. 포인트 조회
        Account account = accountRepository.findByMember_MemberId(userId)
                .orElseThrow(() -> new RuntimeException("계좌 정보 없음"));

        // 2. 상점 아이템 조회
        List<ItemResponse> items = gifticonRepository.findAll().stream()
                .map(ItemResponse::fromEntity)
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
                    .filter(um -> um.getMissionList().getMissionListId().equals(mission.getMissionListId()))
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
        // [수정] findByMember_MemberIdAndMissionList_MissionListId 메서드로 통일 권장
        MemberMission memberMission = memberMissionRepository
                .findByMember_MemberIdAndMissionList_MissionListId(memberId, missionId)
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId).orElseThrow();
                    MissionList mission = missionRepository.findById(missionId).orElseThrow();
                    return MemberMission.builder()
                            .member(member)
                            .missionList(mission)
                            .progressCount(0)
                            .status(CommonEnums.Status.N)
                            .build();
                });
        if (memberMission.getStatus() == CommonEnums.Status.Y) {
            throw new IllegalStateException("이미 보상을 획득한 미션입니다.");
        }

        // ⭐ 추가: 목표 수치에 도달했는지 검증
        if (memberMission.getProgressCount() < memberMission.getMissionList().getGoalCount()) {
            throw new IllegalStateException("미션 목표를 아직 달성하지 못했습니다.");
        }

        Member member = memberMission.getMember();
        Account account = accountRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("계좌 정보가 없습니다."));

        account.deposit(memberMission.getMissionList().getPointAccount());
        memberMission.complete(); // status = Y 처리

        memberMissionRepository.save(memberMission);
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

        if (memberMission.getStatus() == CommonEnums.Status.Y) return;

        // ⭐ 엔티티의 updateProgress 활용
        memberMission.updateProgress(value, isAccumulative);

        // 목표치 초과 방지
        if (memberMission.getProgressCount() > mission.getGoalCount()) {
            memberMission.updateProgress(mission.getGoalCount(), false);
        }

        memberMissionRepository.save(memberMission);
    }
}
