package com.code808.calmdesk.domain.attendance.controller.employee;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.attendance.service.EmployeeAttendanceServiceImpl;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 프론트 Attendance 페이지(/app/attendance)용 API. 요약 카드, 근태 기록 타임라인, 휴가 현황 조회. 인증 연동
 * 전: memberId 쿼리 파라미터(미지정 시 1L) 사용.
 */
@Tag(name = "Attendance", description = "직원 근태 관리 API (요약, 기록 히스토리, 휴가 현황)")
@RestController
@RequestMapping("/api/employee/attendance")
@RequiredArgsConstructor
public class EmployeeAttendanceController {

    private final MemberRepository memberRepository;
    private final EmployeeAttendanceServiceImpl employeeAttendanceService;

    /**
     * GET /api/employee/attendance/summary?year=2026&month=1&memberId=1 이번 달
     * 출근, 지각/결근, 잔여 연차, 이번 주 근무 시간
     */
    @Operation(summary = "근태 요약 조회", description = "이번 달 출근 일수, 지각 건수, 잔여 연차, 이번 주 근무 시간 요약을 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<AttendanceDto.AttendanceSummaryRes> getSummary(
            @Parameter(description = "조회 년도", example = "2026") @RequestParam int year,
            @Parameter(description = "조회 월", example = "1") @RequestParam int month,
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
    @Operation(summary = "근태 기록 히스토리 조회", description = "특정 년/월의 전체 출퇴근 기록 타임라인을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<List<AttendanceDto.AttendanceHistoryItemRes>> getHistory(
            @Parameter(description = "조회 년도", example = "2026") @RequestParam int year,
            @Parameter(description = "조회 월", example = "1") @RequestParam int month,
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
    @Operation(summary = "휴가 신청 현황 조회", description = "본인이 신청한 휴가 내역 및 처리 상태를 조회합니다.")
    @GetMapping("/leaves")
    public ResponseEntity<List<AttendanceDto.LeaveRequestItemRes>> getLeaveRequests(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        List<AttendanceDto.LeaveRequestItemRes> list = employeeAttendanceService.getLeaveRequests(member.getMemberId());
        return ResponseEntity.ok(list);
    }

}
