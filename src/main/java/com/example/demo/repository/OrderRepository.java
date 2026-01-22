package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.repository.custom.OrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, com.example.demo.id.OrderId>, OrderRepositoryCustom {
}
