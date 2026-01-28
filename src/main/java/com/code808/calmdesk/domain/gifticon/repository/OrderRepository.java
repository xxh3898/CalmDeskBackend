package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Order;
import com.code808.calmdesk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMemberOrderByOrderDateDesc(Member member);
}
