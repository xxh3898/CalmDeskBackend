package com.code808.calmdesk.domain.company.repository;

import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByCompany(Company company);

    Optional<Department> findByCompanyAndDepartmentName(Company company, String departmentName);

}