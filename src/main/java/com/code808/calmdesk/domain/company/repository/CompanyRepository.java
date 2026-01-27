package com.code808.calmdesk.domain.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.member.entity.Company;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompanyCode(String companyCode);
    boolean existsByCompanyCode(String companyCode);
}
