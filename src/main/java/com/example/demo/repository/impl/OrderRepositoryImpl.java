package com.example.demo.repository.impl;

import com.example.demo.entity.Member;
import com.example.demo.entity.Order;
import com.example.demo.repository.custom.OrderRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Order> findByMemberOrderByOrderDateDesc(Member member) {
        String jpql = "SELECT o FROM ORDERS o WHERE o.member = :member ORDER BY o.orderDate DESC";
        return entityManager.createQuery(jpql, Order.class)
                .setParameter("member", member)
                .getResultList();
    }
}
