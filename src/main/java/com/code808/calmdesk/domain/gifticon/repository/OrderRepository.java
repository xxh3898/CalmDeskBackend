package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.GiftOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<GiftOrder, Long> {
    // 특정 회원의 주문 내역만 최신순으로 조회하고 싶을 때
    List<GiftOrder> findByMemberIdOrderByOrderDateDesc(Long memberId);
}