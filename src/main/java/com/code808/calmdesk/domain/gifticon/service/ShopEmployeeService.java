package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.gifticon.dto.*;
import com.code808.calmdesk.domain.gifticon.entity.*;
import com.code808.calmdesk.domain.member.entity.Account;
import com.code808.calmdesk.domain.gifticon.repository.*;
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

        // 3. 미션 목록 조회
        List<MissionResponse> missions = missionRepository.findAll().stream()
                .map(MissionResponse::fromEntity)
                .collect(Collectors.toList());

        return PointMallResponse.builder()
                .currentPoint(account.getAccountLeave())
                .shopItems(items)
                .missions(missions)
                .build();
    }
}