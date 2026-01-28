package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.enums.CommonEnums;
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

    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        // ... (기존 purchase 로직과 동일)
        Gifticon gifticon = gifticonRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        if (gifticon.getQuantity() <= 0) {
            throw new RuntimeException("상품 재고가 없습니다.");
        }
        gifticon.setQuantity(gifticon.getQuantity() - 1);

        // findByMember_MemberId의 매개변수 타입을 확인하세요 (String vs Long)
        Account account = accountRepository.findByMember_MemberId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("계좌 정보를 찾을 수 없습니다."));

        account.withdraw(request.getPrice().intValue());

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

        return orderRepository.save(order).getOrderId();
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



    // 미션을 불러오는 로직
    @Transactional(readOnly = true)
    public List<MissionResponse> getAllMissions(Long userId) {
        List<MissionList> allMissions = missionRepository.findByStatus(CommonEnums.Status.Y);

        // [수정] 메서드명 변경: findByUser_UserId -> findByMember_MemberId
        List<MemberMission> userMissions = memberMissionRepository.findByMember_MemberId(userId);

        // 사용자의 미션에 대해 완료한건지 아닌지를 판단하여 사용자에게 보여줄 미션의 상태를 변환하는 로직이다!!
        return allMissions.stream().map(mission -> {
            String status = userMissions.stream()
                    .filter(um -> um.getMissionList().getMissionListId().equals(mission.getMissionListId()))
                    .map(um -> um.getStatus().name())
                    .findFirst()
                    .orElse("N");

            return MissionResponse.builder()
                    .id(mission.getMissionListId())
                    .title(mission.getRewardName())
                    .description(mission.getRewardDescription())
                    .reward(mission.getPointAccount())
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void completeMission(Long memberId, Long missionId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        MissionList mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new EntityNotFoundException("미션 정보가 없습니다."));

        // [수정] 위에서 만든 명시적인 쿼리 메서드 호출
        MemberMission memberMission = memberMissionRepository
                .findMemberMission(memberId, missionId)
                .orElseGet(() -> MemberMission.builder()
                        .member(member)
                        .missionList(mission)
                        .status(CommonEnums.Status.N)
                        .build());

        if (memberMission.getStatus() == CommonEnums.Status.Y) {
            throw new IllegalStateException("이미 보상을 획득한 미션입니다.");
        }

        Account account = member.getAccount();
        if (account == null) {
            throw new IllegalStateException("계좌 정보가 없습니다.");
        }

        account.deposit(mission.getPointAccount());
        memberMission.complete();

        memberMissionRepository.save(memberMission);
    }
}
