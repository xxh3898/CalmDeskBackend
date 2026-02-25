package com.code808.calmdesk.domain.dashboard.service.employee;

import com.code808.calmdesk.domain.dashboard.dto.employee.EmotionCheckInRequest;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;

public interface EmployeeDashboardService {

    EmployeeDashboardResponseDto getDashboardData(Long memberId);

    void clockIn(Long memberId, EmotionCheckInRequest request);

    void clockOut(Long memberId, EmotionCheckInRequest request);

    void updateStatus(Long memberId, String status);

    void startCoolDown(Long memberId, EmotionCheckInRequest request);
}
