package com.code808.calmdesk.domain.company.service;

import java.util.List;

import com.code808.calmdesk.domain.company.dto.DepartmentMemberDto;
import com.code808.calmdesk.domain.company.dto.DepartmentResponseDto;

public interface DepartmentService {

    DepartmentResponseDto getDepartmentDetails(Long departmentId);

    List<DepartmentMemberDto> getDepartmentMembers(Long departmentId);
}
