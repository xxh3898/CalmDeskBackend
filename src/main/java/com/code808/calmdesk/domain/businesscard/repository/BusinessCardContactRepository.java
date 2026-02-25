package com.code808.calmdesk.domain.businesscard.repository;

import com.code808.calmdesk.domain.businesscard.entity.BusinessCardContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessCardContactRepository extends JpaRepository<BusinessCardContact, Long> {

    List<BusinessCardContact> findByCompany_CompanyIdOrderByCreatedDateDesc(Long companyId);

    Optional<BusinessCardContact> findByCompany_CompanyIdAndPhone(Long companyId, String phone);

    Optional<BusinessCardContact> findByCompany_CompanyIdAndEmail(Long companyId, String email);
}
