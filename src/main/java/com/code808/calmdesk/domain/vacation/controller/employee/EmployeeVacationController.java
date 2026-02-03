package com.code808.calmdesk.domain.vacation.controller.employee;


import com.code808.calmdesk.domain.vacation.dto.VacationDto;
import com.code808.calmdesk.domain.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/vacation")
@RequiredArgsConstructor
public class EmployeeVacationController {

    private final VacationService vacationService;

    /**
     * POST /api/employee/vacation?memberId=1
     * 휴가 신청
     */
    @PostMapping
    public ResponseEntity<VacationDto.VacationRequestRes> requestVacation(
            @RequestParam(required = false, defaultValue = "1") Long memberId,
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
     * DELETE /api/employee/vacation/{vacationId}?memberId=1
     * 휴가 취소 (직원용 - 승인 대기 상태만 취소 가능)
     */
    @DeleteMapping("/{vacationId}")
    public ResponseEntity<VacationDto.VacationRequestRes> cancelVacation(
            @PathVariable Long vacationId,
            @RequestParam(required = false, defaultValue = "1") Long memberId) {
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
