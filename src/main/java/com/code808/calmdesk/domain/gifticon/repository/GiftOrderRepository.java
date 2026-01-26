package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftOrderRepository extends JpaRepository<Order, Long> {
}