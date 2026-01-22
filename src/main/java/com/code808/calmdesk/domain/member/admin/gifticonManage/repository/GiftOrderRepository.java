package com.code808.calmdesk.domain.member.admin.gifticonManage.repository;

import com.code808.calmdesk.domain.member.admin.gifticonManage.entity.Gift_Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftOrderRepository extends JpaRepository<Gift_Order, Long> {
    // 기본 CRUD(findAll, findById, save 등) 자동 생성됨
}