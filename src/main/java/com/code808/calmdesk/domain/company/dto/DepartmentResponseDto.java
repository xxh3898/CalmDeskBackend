package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.member.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDto {
    private Long departmentId;
    private String departmentName;
    private Long companyId;
    private int memberCount;

    public static DepartmentResponseDto from(Department department) {
        return DepartmentResponseDto.builder()
                .departmentId(department.getDepartmentId())
                .departmentName(department.getDepartmentName())
                .companyId(department.getCompany().getCompanyId())
                .memberCount(department.getMembers().size())
                .build();
    }
}
