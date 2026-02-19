package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.CompanyGifticon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CompanyGifticonRepository extends JpaRepository<CompanyGifticon, Long> {


    List<CompanyGifticon> findAllByCompany_CompanyIdAndIsActiveTrue(Long companyId);

    // 구매 시 특정 기프티콘 설정 찾기
    Optional<CompanyGifticon> findByCompany_CompanyIdAndGifticon_Id(Long companyId, Long gifticonId);
    // 1. 특정 회사의 활성화된 기프티콘 목록 조회 (기프티콘 정보까지 페치 조인으로 한 번에 가져오기)
    @Query("SELECT cg FROM CompanyGifticon cg JOIN FETCH cg.gifticon " +
            "WHERE cg.company.companyId = :companyId AND cg.isActive = true")
    List<CompanyGifticon> findAllActiveByCompanyId(@Param("companyId") Long companyId);

    // 3. 재고가 있는 상품만 조회하는 기능
    List<CompanyGifticon> findByCompany_CompanyIdAndStockQuantityGreaterThan(Long companyId, int quantity);


    // 관리자용: 해당 회사의 모든 설정 데이터 조회
    List<CompanyGifticon> findAllByCompany_CompanyId(Long companyId);

    // 일괄 업데이트를 위한 쿼리 (선택 사항)
    @Modifying
    @Query("UPDATE CompanyGifticon cg SET cg.isActive = :status WHERE cg.company.companyId = :companyId")
    void updateAllStatusByCompanyId(@Param("companyId") Long companyId, @Param("status") Boolean status);

}