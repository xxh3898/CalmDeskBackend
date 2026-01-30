package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import com.code808.calmdesk.domain.gifticon.entity.Order;
import com.code808.calmdesk.domain.gifticon.entity.Point_History;
import com.code808.calmdesk.domain.gifticon.repository.ItemRepository;
import com.code808.calmdesk.domain.gifticon.repository.OrderRepository;
import com.code808.calmdesk.domain.gifticon.repository.PointHistoryRepository;
import com.code808.calmdesk.domain.member.entity.Account;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.AccountRepository;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));
        Gifticon gifticon = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));

        Order order = Order.builder()
                .member(member)
                .gifticon(gifticon)
                .period(30)
                .approvalAmount(request.getPrice().intValue())
                .orderDate(LocalDate.now())
                .build();
        Order savedOrder = orderRepository.save(order);

        long currentBalance = getCurrentBalance(request.getUserId());
        long spendAmount = request.getPrice() != null ? request.getPrice() : 0L;
        long balanceAfter = Math.max(0, currentBalance - spendAmount);

        Point_History history = new Point_History(
                "SPEND",
                spendAmount,
                balanceAfter,
                "GIFTICON",
                request.getUserId(),
                request.getItemId(),
                null
        );
        pointHistoryRepository.save(history);

        accountRepository.findByMemberMemberId(request.getUserId()).ifPresent(account -> {
            account.setRemainingPoint(balanceAfter);
            account.setTotalSpent(account.getTotalSpent() + spendAmount);
        });

        return savedOrder.getId();
    }

    /** 포인트 내역이 있으면 최신 balanceAfter, 없으면 ACCOUNT 잔액, 없으면 0 */
    private long getCurrentBalance(Long memberId) {
        List<Point_History> histories = pointHistoryRepository.findByMemberIdOrderByCreateDateDescIdDesc(memberId);
        if (!histories.isEmpty() && histories.get(0).getBalanceAfter() != null) {
            return histories.get(0).getBalanceAfter();
        }
        return accountRepository.findByMemberMemberId(memberId)
                .map(a -> a.getRemainingPoint() != null ? a.getRemainingPoint() : 0L)
                .orElse(0L);
    }

    @Transactional(readOnly = true)
    public List<Gifticon> findAllGifiticon() {
        return itemRepository.findAll();
    }

    @Transactional
    public void toggleActive(Long id) {
        Gifticon item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다. id=" + id));
        item.setStatus(item.getStatus() == CommonEnums.Status.Y ? CommonEnums.Status.N : CommonEnums.Status.Y);
    }

    @Transactional
    public void activateAll() {
        itemRepository.activateAllItems(CommonEnums.Status.Y);
    }

    @Transactional
    public void deactivateAll() {
        itemRepository.deactivateAllItems(CommonEnums.Status.N);
    }

    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        Gifticon item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다. id=" + id));
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0보다 작을 수 없습니다.");
        }
        item.setStockQuantity(quantity);
    }
}
