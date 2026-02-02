package com.code808.calmdesk.domain.attendance.controller.employee;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.attendance.service.EmployeeAttendanceService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.dto.VacationDto;

import lombok.RequiredArgsConstructor;

/**
 * 프론트 Attendance 페이지(/app/attendance)용 API. 요약 카드, 근태 기록 타임라인, 휴가 현황 조회. 인증 연동
 * 전: memberId 쿼리 파라미터(미지정 시 1L) 사용.
 */
@RestController
@RequestMapping("/api/employee/attendance")
@RequiredArgsConstructor
public class EmployeeAttendanceController {

    private final MemberRepository memberRepository;
    private final EmployeeAttendanceService employeeAttendanceService;

    /**
     * GET /api/employee/attendance/summary?year=2026&month=1&memberId=1 이번 달
     * 출근, 지각/결근, 잔여 연차, 이번 주 근무 시간
     */
    @GetMapping("/summary")
    public ResponseEntity<AttendanceDto.AttendanceSummaryRes> getSummary(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        AttendanceDto.AttendanceSummaryRes res = employeeAttendanceService.getSummary(member.getMemberId(), year, month);
        return ResponseEntity.ok(res);
    }

    /**
     * GET /api/employee/attendance/history?year=2026&month=1&memberId=1 전체 기록
     * 타임라인 / 일별 상세용 (id, day, date, clockIn, clockOut, status, duration, note)
     */
    @GetMapping("/history")
    public ResponseEntity<List<AttendanceDto.AttendanceHistoryItemRes>> getHistory(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        List<AttendanceDto.AttendanceHistoryItemRes> list = employeeAttendanceService.getHistory(member.getMemberId(), year, month);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/employee/attendance/leaves?memberId=1 휴가 현황 (id, type, period,
     * status, days)
     */
    @GetMapping("/leaves")
    public ResponseEntity<List<AttendanceDto.LeaveRequestItemRes>> getLeaveRequests(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        List<AttendanceDto.LeaveRequestItemRes> list = employeeAttendanceService.getLeaveRequests(member.getMemberId());
        return ResponseEntity.ok(list);
    }

    /**
     * POST /api/employee/attendance/vacation?memberId=1 휴가 신청
     */
    @PostMapping("/vacation")
    public ResponseEntity<VacationDto.VacationRequestRes> requestVacation(
            @RequestParam(required = false, defaultValue = "1") Long memberId,
            @RequestBody VacationDto.VacationRequestReq req) {
        try {
            VacationDto.VacationRequestRes res = employeeAttendanceService.requestVacation(memberId, req);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            // 유효성 검사 실패 시 400 Bad Request
            return ResponseEntity.badRequest()
                    .body(VacationDto.VacationRequestRes.builder()
                            .id(null)
                            .message(e.getMessage())
                            .build());
        }
    }

    /**
     * PUT
     * /api/employee/attendance/vacation/{vacationId}/approve?approverMemberId=2
     * 휴가 승인 (관리자용)
     */
    @PutMapping("/vacation/{vacationId}/approve")
    public ResponseEntity<VacationDto.VacationRequestRes> approveVacation(
            @PathVariable Long vacationId,
            @RequestParam(required = false, defaultValue = "2") Long approverMemberId) {
        try {
            VacationDto.VacationRequestRes res = employeeAttendanceService.approveVacation(vacationId, approverMemberId);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(VacationDto.VacationRequestRes.builder()
                            .id(null)
                            .message(e.getMessage())
                            .build());
        }
    }
}
