package com.code808.calmdesk.domain.businesscard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.businesscard.entity.BusinessCardContact;

public interface BusinessCardContactRepository extends JpaRepository<BusinessCardContact, Long> {

    Page<BusinessCardContact> findByCompany_CompanyIdOrderByCreatedDateDesc(Long companyId, Pageable pageable);

    Optional<BusinessCardContact> findByCompany_CompanyIdAndPhone(Long companyId, String phone);

    Optional<BusinessCardContact> findByCompany_CompanyIdAndEmail(Long companyId, String email);
}
