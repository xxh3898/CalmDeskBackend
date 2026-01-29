package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GifticonRepository extends JpaRepository<Gifticon, Long> {

    @Query("SELECT g FROM GIFTICON g WHERE g.company.companyId = :companyId")
    List<Gifticon> findAllByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Transactional
    @Query("UPDATE GIFTICON g SET g.status = com.code808.calmdesk.domain.common.enums.CommonEnums.Status.Y")
    void activateAllItems();

    @Modifying
    @Transactional
    @Query("UPDATE GIFTICON g SET g.status = com.code808.calmdesk.domain.common.enums.CommonEnums.Status.N")
    void deactivateAllItems();
}
