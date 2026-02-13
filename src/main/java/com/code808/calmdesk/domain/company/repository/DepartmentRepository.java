package com.code808.calmdesk.domain.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByCompany(Company company);

    Optional<Department> findByCompanyAndDepartmentName(Company company, String departmentName);

    Optional<Department> findByDepartmentIdAndCompany_CompanyId(Long departmentId, Long companyId);

    List<Department> findByCompany_CompanyId(Long companyId);

}
