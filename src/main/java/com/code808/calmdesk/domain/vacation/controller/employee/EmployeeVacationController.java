package com.code808.calmdesk.domain.vacation.controller.employee;

import com.code808.calmdesk.domain.vacation.dto.VacationDto;
import com.code808.calmdesk.domain.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Vacation Employee", description = "직원용 휴가 관리 API (신청, 취소)")
@RestController
@RequestMapping("/api/employee/vacation")
@RequiredArgsConstructor
public class EmployeeVacationController {

    private final VacationService vacationService;

    /**
     * POST /api/employee/vacation?memberId=1 휴가 신청
     */
    @Operation(summary = "휴가 신청", description = "새로운 휴가 신청을 생성합니다.")
    @PostMapping
    public ResponseEntity<VacationDto.VacationRequestRes> requestVacation(
            @Parameter(description = "사용자 ID (선택)", example = "1") @RequestParam(required = false, defaultValue = "1") Long memberId,
            @RequestBody VacationDto.VacationRequestReq req) {
        try {
            VacationDto.VacationRequestRes res = vacationService.requestVacation(memberId, req);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(VacationDto.VacationRequestRes.builder()
                            .id(null)
                            .message(e.getMessage())
                            .build());
        }
    }

    /**
     * DELETE /api/employee/vacation/{vacationId}?memberId=1 휴가 취소 (직원용 - 승인 대기
     * 상태만 취소 가능)
     */
    @Operation(summary = "휴가 취소", description = "승인 대기 중인 휴가 신청을 취소합니다.")
    @DeleteMapping("/{vacationId}")
    public ResponseEntity<VacationDto.VacationRequestRes> cancelVacation(
            @Parameter(description = "휴가 ID", example = "50") @PathVariable Long vacationId,
            @Parameter(description = "사용자 ID (선택)", example = "1") @RequestParam(required = false, defaultValue = "1") Long memberId) {
        try {
            VacationDto.VacationRequestRes res = vacationService.cancelVacation(vacationId, memberId);
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
