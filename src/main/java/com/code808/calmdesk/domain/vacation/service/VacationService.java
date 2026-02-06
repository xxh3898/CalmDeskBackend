package com.code808.calmdesk.domain.vacation.service;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.vacation.dto.VacationDto;

import java.util.List;

public interface VacationService {
    VacationDto.VacationRequestRes requestVacation(Long memberId, VacationDto.VacationRequestReq req);
    VacationDto.VacationRequestRes approveVacation(Long vacationId, Long approverMemberId);
    List<AttendanceDto.LeaveRequestItemRes> getLeaveRequestsByCompany(Long companyId);
    VacationDto.VacationRequestRes cancelVacation(Long vacationId, Long memberId);
    VacationDto.VacationRequestRes rejectVacation(Long vacationId);
}
