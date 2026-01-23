package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface GifticonRepository extends JpaRepository<Gifticon, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE Gifticon g SET g.isActive = true")
    void activateAllItems();

    @Modifying
    @Transactional
    @Query("UPDATE Gifticon g SET g.isActive = false")
    void deactivateAllItems();
}
