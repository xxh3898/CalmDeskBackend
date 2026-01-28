package com.code808.calmdesk.domain.attendance.controller.employee;

import com.code808.calmdesk.domain.attendance.dto.*;
import com.code808.calmdesk.domain.attendance.service.EmployeeAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 프론트 Attendance 페이지(/app/attendance)용 API.
 * 요약 카드, 근태 기록 타임라인, 휴가 현황 조회.
 * 인증 연동 전: memberId 쿼리 파라미터(미지정 시 1L) 사용.
 */
@RestController
@RequestMapping("/api/employee/attendance")
@RequiredArgsConstructor
public class EmployeeAttendanceController {

    private final EmployeeAttendanceService employeeAttendanceService;

    /**
     * GET /api/employee/attendance/summary?year=2026&month=1&memberId=1
     * 이번 달 출근, 지각/결근, 잔여 연차, 이번 주 근무 시간
     */
    @GetMapping("/summary")
    public ResponseEntity<AttendanceSummaryRes> getSummary(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false, defaultValue = "1") Long memberId) {
        AttendanceSummaryRes res = employeeAttendanceService.getSummary(memberId, year, month);
        return ResponseEntity.ok(res);
    }

    /**
     * GET /api/employee/attendance/history?year=2026&month=1&memberId=1
     * 전체 기록 타임라인 / 일별 상세용 (id, day, date, clockIn, clockOut, status, duration, note)
     */
    @GetMapping("/history")
    public ResponseEntity<List<AttendanceHistoryItemRes>> getHistory(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false, defaultValue = "1") Long memberId) {
        List<AttendanceHistoryItemRes> list = employeeAttendanceService.getHistory(memberId, year, month);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/employee/attendance/leaves?memberId=1
     * 휴가 현황 (id, type, period, status, days)
     */
    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequestItemRes>> getLeaveRequests(
            @RequestParam(required = false, defaultValue = "1") Long memberId) {
        List<LeaveRequestItemRes> list = employeeAttendanceService.getLeaveRequests(memberId);
        return ResponseEntity.ok(list);
    }

    /**
     * POST /api/employee/attendance/vacation?memberId=1
     * 휴가 신청
     */
    @PostMapping("/vacation")
    public ResponseEntity<VacationRequestRes> requestVacation(
            @RequestParam(required = false, defaultValue = "1") Long memberId,
            @RequestBody VacationRequestReq req) {
        try {
            VacationRequestRes res = employeeAttendanceService.requestVacation(memberId, req);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            // 유효성 검사 실패 시 400 Bad Request
            return ResponseEntity.badRequest()
                    .body(VacationRequestRes.builder()
                            .id(null)
                            .message(e.getMessage())
                            .build());
        }
    }

    /**
     * PUT /api/employee/attendance/vacation/{vacationId}/approve?approverMemberId=2
     * 휴가 승인 (관리자용)
     */
    @PutMapping("/vacation/{vacationId}/approve")
    public ResponseEntity<VacationRequestRes> approveVacation(
            @PathVariable Long vacationId,
            @RequestParam(required = false, defaultValue = "2") Long approverMemberId) {
        try {
            VacationRequestRes res = employeeAttendanceService.approveVacation(vacationId, approverMemberId);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(VacationRequestRes.builder()
                            .id(null)
                            .message(e.getMessage())
                            .build());
        }
    }
}
