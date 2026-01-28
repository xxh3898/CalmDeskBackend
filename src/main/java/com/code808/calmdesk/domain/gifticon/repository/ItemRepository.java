package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Gifticon, Long> {

    @Modifying
    @Query("UPDATE GIFTICON g SET g.stockQuantity = g.stockQuantity - 1 WHERE g.gifticonId = :id AND g.stockQuantity > 0")
    int decreaseQuantity(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GIFTICON g SET g.status = :status")
    void activateAllItems(@Param("status") CommonEnums.Status status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE GIFTICON g SET g.status = :status")
    void deactivateAllItems(@Param("status") CommonEnums.Status status);
}
