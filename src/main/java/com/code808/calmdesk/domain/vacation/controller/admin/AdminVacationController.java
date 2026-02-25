package com.code808.calmdesk.domain.vacation.controller.admin;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.dto.VacationDto.VacationRequestRes;
import com.code808.calmdesk.domain.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.List;

/**
 * 관리자용 휴가 신청 조회 및 처리 API GET 목록, PUT 승인/반려
 */
@Tag(name = "Vacation Admin", description = "관리자용 휴가 신청 관리 API (목록 조회, 승인/반려)")
@RestController
@RequestMapping("/api/admin/vacation")
@RequiredArgsConstructor
public class AdminVacationController {

    private final MemberRepository memberRepository;
    private final VacationService vacationService;

    /**
     * GET /api/admin/vacation 현재 로그인한 관리자 회사의 전체 휴가 신청 목록
     */
    @Operation(summary = "휴가 신청 목록 조회", description = "관리자 소속 회사의 모든 휴가 신청 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AttendanceDto.LeaveRequestItemRes>> getLeaves(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (member.getCompany() == null) {
            return ResponseEntity.ok(List.of());
        }
        List<AttendanceDto.LeaveRequestItemRes> list = vacationService.getLeaveRequestsByCompany(member.getCompany().getCompanyId());
        return ResponseEntity.ok(list);
    }

    /**
     * PUT /api/admin/vacation/{vacationId}/approve 휴가 승인 (현재 로그인 사용자를 승인자로 사용)
     */
    @Operation(summary = "휴가 승인", description = "특정 사용자의 휴가 신청을 승인합니다.")
    @PutMapping("/{vacationId}/approve")
    public ResponseEntity<VacationRequestRes> approveLeave(
            @Parameter(description = "휴가 ID", example = "50") @PathVariable Long vacationId, Principal principal) {
        var member = memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        try {
            VacationRequestRes res = vacationService.approveVacation(vacationId, member.getMemberId());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(VacationRequestRes.builder().id(null).message(e.getMessage()).build());
        }
    }

    /**
     * PUT /api/admin/vacation/{vacationId}/reject 휴가 반려 (승인과 동일하게 Principal 주입해
     * 요청 처리 방식 통일)
     */
    @Operation(summary = "휴가 반려", description = "특정 사용자의 휴가 신청을 반려합니다.")
    @PutMapping("/{vacationId}/reject")
    public ResponseEntity<VacationRequestRes> rejectLeave(
            @Parameter(description = "휴가 ID", example = "50") @PathVariable Long vacationId,
            Principal principal) {
        try {
            VacationRequestRes res = vacationService.rejectVacation(vacationId);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(VacationRequestRes.builder().id(null).message(e.getMessage()).build());
        }
    }
}
