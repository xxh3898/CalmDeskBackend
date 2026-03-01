package com.code808.calmdesk.domain.gifticon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.gifticon.entity.Order;
import com.code808.calmdesk.domain.member.entity.Member;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 회원의 주문 내역만 최신순으로 조회하고 싶을 때
    @Query("SELECT o FROM ORDERS o JOIN FETCH o.member JOIN FETCH o.gifticon LEFT JOIN FETCH o.missionList "
            + "WHERE o.member.memberId = :memberId ORDER BY o.createdDate DESC")
    List<Order> findByMember_MemberIdOrderByCreatedDateDesc(@Param("memberId") Long memberId);

    @Query("SELECT o FROM ORDERS o JOIN FETCH o.member JOIN FETCH o.gifticon LEFT JOIN FETCH o.missionList "
            + "WHERE o.member = :member ORDER BY o.orderDate DESC")
    List<Order> findByMemberOrderByOrderDateDesc(@Param("member") Member member);
}
