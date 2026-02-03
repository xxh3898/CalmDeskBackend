package com.code808.calmdesk.domain.attendance.controller.employee;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.attendance.service.EmployeeAttendanceServiceImpl;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    private final MemberRepository memberRepository;
    private final EmployeeAttendanceServiceImpl employeeAttendanceService;

    /**
     * GET /api/employee/attendance/summary?year=2026&month=1&memberId=1
     * 이번 달 출근, 지각/결근, 잔여 연차, 이번 주 근무 시간
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
     * GET /api/employee/attendance/history?year=2026&month=1&memberId=1
     * 전체 기록 타임라인 / 일별 상세용 (id, day, date, clockIn, clockOut, status, duration, note)
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
     * GET /api/employee/attendance/leaves?memberId=1
     * 휴가 현황 (id, type, period, status, days)
     */
    @GetMapping("/leaves")
    public ResponseEntity<List<AttendanceDto.LeaveRequestItemRes>> getLeaveRequests(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        List<AttendanceDto.LeaveRequestItemRes> list = employeeAttendanceService.getLeaveRequests(member.getMemberId());
        return ResponseEntity.ok(list);
    }

}
