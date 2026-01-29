package com.code808.calmdesk.domain.dashboard.service.employee;

import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;

public interface EmployeeDashboardService {

    EmployeeDashboardResponseDto getDashboardData(Long memberId);

    void clockIn(Long memberId);

    void clockOut(Long memberId);

    void updateStatus(Long memberId, String status);
}
