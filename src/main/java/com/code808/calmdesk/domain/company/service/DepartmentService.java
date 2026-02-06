package com.code808.calmdesk.domain.company.service;

import java.util.List;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;

public interface DepartmentService {

    DepartmentDto.DetailResponse getDepartmentDetails(Long departmentId);

    List<DepartmentDto.MemberResponse> getDepartmentMembers(Long departmentId);

    /** 회사 소속인 경우에만 부서 상세 반환 (companyId로 검증) */
    DepartmentDto.DetailResponse getDepartmentDetailsByCompany(Long departmentId, Long companyId);

    /** 회사 소속인 경우에만 부서 멤버 목록 반환 (companyId로 검증) */
    List<DepartmentDto.MemberResponse> getDepartmentMembersByCompany(Long departmentId, Long companyId);
}
