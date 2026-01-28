package com.code808.calmdesk.domain.company.service;

import java.util.List;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;

public interface DepartmentService {

    DepartmentDto.DetailResponse getDepartmentDetails(Long departmentId);

    List<DepartmentDto.MemberResponse> getDepartmentMembers(Long departmentId);
}
