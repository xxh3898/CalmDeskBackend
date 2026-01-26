package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Gifticon, Long> {

    // 1. id -> gifticonId로 수정 (엔티티의 PK 필드명)
    @Modifying
    @Query("UPDATE GIFTICON g SET g.quantity = g.quantity - 1 WHERE g.gifticonId = :id AND g.quantity > 0")
    int decreaseQuantity(@Param("id") Long id);

    // 2. isActive -> status로 수정 (Enum 타입 반영)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE GIFTICON g SET g.status = com.code808.calmdesk.domain.enums.CommonEnums.Status.Y")
    void activateAllItems();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GIFTICON g SET g.status = com.code808.calmdesk.domain.enums.CommonEnums.Status.N")
    void deactivateAllItems();
}