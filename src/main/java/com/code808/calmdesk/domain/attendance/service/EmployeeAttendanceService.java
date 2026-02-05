package com.code808.calmdesk.domain.attendance.service;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;

import java.util.List;

public interface EmployeeAttendanceService {

    AttendanceDto.AttendanceSummaryRes getSummary(Long memberId, int year, int month);
    List<AttendanceDto.AttendanceHistoryItemRes> getHistory(Long memberId, int year, int month);
    List<AttendanceDto.LeaveRequestItemRes> getLeaveRequests(Long memberId);

}
