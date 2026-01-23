package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Gifticon, Long> {

    // 재고 수량을 1 감소시키는 커스텀 쿼리 (동시성 문제 방지)
    @Modifying
    @Query("UPDATE Gifticon g SET g.quantity = g.quantity - 1 WHERE g.id = :id AND g.quantity > 0")
    int decreaseQuantity(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Gifticon g SET g.isActive = true")
    void activateAllItems();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Gifticon g SET g.isActive = false")
    void deactivateAllItems();
}