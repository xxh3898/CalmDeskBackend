package com.example.demo.repository.custom;

import com.example.demo.entity.Member;
import com.example.demo.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findByMemberOrderByOrderDateDesc(Member member);
}
