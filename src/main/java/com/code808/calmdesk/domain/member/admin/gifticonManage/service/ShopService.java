package com.code808.calmdesk.domain.member.admin.gifticonManage.service;


import com.code808.calmdesk.domain.member.admin.gifticonManage.dto.PurchaseRequest;
import com.code808.calmdesk.domain.member.admin.gifticonManage.entity.Gift_Order;
import com.code808.calmdesk.domain.member.admin.gifticonManage.entity.Gifticon;
import com.code808.calmdesk.domain.member.admin.gifticonManage.entity.Point_History;
import com.code808.calmdesk.domain.member.admin.gifticonManage.repository.GiftOrderRepository;
import com.code808.calmdesk.domain.member.admin.gifticonManage.repository.ItemRepository;
import com.code808.calmdesk.domain.member.admin.gifticonManage.repository.MemberRepository;
import com.code808.calmdesk.domain.member.admin.gifticonManage.repository.PointHistoryRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final GiftOrderRepository giftOrderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ItemRepository itemRepository; // 기프티콘 정보 테이블
    private final MemberRepository memberRepository;


    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        // [비즈니스 로직]
        // 1. 재고 확인 (생략 가능하나 권장)
        // 2. 유저 포인트 잔액 확인 (부족 시 throw new RuntimeException("포인트 부족"))

        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // 3. 생성자에 request.getUserId() 대신 조회한 'member' 객체를 전달
        Gift_Order order = new Gift_Order(
                request.getItemId(),
                member,           // Long -> Member 객체로 변경
                30,
                request.getPrice()
        );
        Gift_Order savedOrder = giftOrderRepository.save(order);

        // 2. POINT_HISTORY 저장 (차감 내역)
        Point_History history = new Point_History(
                "SPEND",          // 포인트 종류
                request.getPrice(), // 금액
                95500L,           // 계산된 잔액 (실제로는 DB 조회 후 계산)
                "GIFTICON",       // 출처 유형
                request.getUserId(),
                request.getItemId(),
                null              // 미션 아이디는 없음
        );
        pointHistoryRepository.save(history);

        // 3. 재고 차감 (선택 사항)
        // itemRepository.decreaseQuantity(request.getItemId());

        return savedOrder.getId();
    }

    @Transactional(readOnly = true)
    public List<Gifticon> findAllGifiticon() {
        return itemRepository.findAll();
    }


    @Transactional
    public void toggleActive(Long id) {
        // 아이템 찾아서 isActive를 !isActive로 변경하는 로직
        // 1. 아이템 조회 (없으면 예외 발생)
        Gifticon item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + id));
        // 2. 현재 상태 반전 (isActive가 boolean 타입일 경우)
        // 만약 필드명이 아까 확인한 대로 active라면 item.isActive() 대신 item.isActive() 등을 사용하세요.
        item.setActive(!item.isActive());

        // @Transactional이 붙어있으므로 여기서 save()를 명시적으로 호출하지 않아도
        // 메서드가 끝날 때 DB에 UPDATE 쿼리가 날아갑니다.
    }

    @Transactional
    public void activateAll() {
        itemRepository.activateAllItems();
    }

    @Transactional
    public void deactivateAll() {
        itemRepository.deactivateAllItems();
    }



    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        Gifticon item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다. id=" + id));

        // 2. 수량 검증 (선택 사항: 0보다 작은지 체크)
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0보다 작을 수 없습니다.");
        }
        // 3. 수량 변경 (Dirty Checking으로 인해 자동 저장됨)
        item.setQuantity(quantity);
    }
}