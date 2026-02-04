package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import com.code808.calmdesk.domain.gifticon.dto.ItemResponse;
import com.code808.calmdesk.domain.gifticon.entity.Order;
import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import com.code808.calmdesk.domain.gifticon.entity.PointHistory;
import com.code808.calmdesk.domain.gifticon.repository.OrderRepository;
import com.code808.calmdesk.domain.gifticon.repository.GifticonRepository;
import com.code808.calmdesk.domain.gifticon.repository.PointHistoryRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopAdminService {

    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final GifticonRepository gifticonRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        // 1. 회원 및 아이템 조회
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        Gifticon gifticon = gifticonRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));

        // 2. 포인트 잔액 확인 및 차감 로직 (예시)
        long price = request.getPrice();
        // 가상의 메서드: member.getPoints()가 있다고 가정
        // if (member.getPoints() < price) throw new RuntimeException("포인트가 부족합니다.");

        // 계산된 잔액 (현재 잔액 - 상품 가격)
        // 실제 프로젝트에서는 Member 엔터티 내부에 포인트 차감 로직을 두는 것이 좋습니다.
        Long balanceAfter = 95500L; // TODO: member.getPoints() - price;

        // 3. Order 생성 및 저장
        Order order = Order.builder()
                .gifticon(gifticon)
                .member(member)
                .period(30)
                .approvalAmount((int) price)
                .spendPoint((int) price)
                .earnPoint(0)
                .type(Order.Type.SPEND)
                .orderDate(java.time.LocalDate.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 4. POINT_HISTORY 저장 (핵심 수정 부분! 🚀)
        // 수정된 생성자: Long ID가 아니라 조회한 'member'와 'gifticon' 객체를 직접 전달합니다.
        PointHistory history = new PointHistory(
                "SPEND",        // 포인트 종류
                price,          // 금액
                balanceAfter,   // 계산된 잔액
                "GIFTICON",     // 출처 유형
                member,         // 👈 객체 전달 (memberId 대신)
                gifticon,       // 👈 객체 전달 (gifticonId 대신)
                null            // 미션 아이디 없음
        );
        pointHistoryRepository.save(history);

        return savedOrder.getOrderId();
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findAllByCompany(Long companyId) {
        // 레포지토리에서 회사 ID를 기준으로 조회
        return gifticonRepository.findAllByCompanyId(companyId)
                .stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleStatus(Long id) {
        // 1. 미션 리스트 조회
        Gifticon gifticon = gifticonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 미션이 존재하지 않습니다. id=" + id));

        // 2. Enum 상태 반전 (Y <-> N)
        if (gifticon.getStatus() == CommonEnums.Status.Y) {
            gifticon.setStatus(CommonEnums.Status.N);
        } else {
            gifticon.setStatus(CommonEnums.Status.Y);
        }

        // @Transactional에 의해 변경 감지(Dirty Checking)가 일어나며 자동 반영됩니다.
    }

    @Transactional
    public void activateAll() {
        gifticonRepository.activateAllItems();
    }

    @Transactional
    public void deactivateAll() {
        gifticonRepository.deactivateAllItems();
    }

    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        Gifticon item = gifticonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다. id=" + id));

        // 2. 수량 검증 (선택 사항: 0보다 작은지 체크)
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0보다 작을 수 없습니다.");
        }
        // 3. 수량 변경 (Dirty Checking으로 인해 자동 저장됨)
        item.setStockQuantity(quantity);
    }
}