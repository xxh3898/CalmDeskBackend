package com.code808.calmdesk.domain.consultation.controller.admin;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.consultation.dto.ConsultationDto;
import com.code808.calmdesk.domain.consultation.service.ConsultationService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 관리자용 상담 신청 조회 및 처리 API (휴가/입사 신청과 동일한 흐름) GET 목록(회사별), PUT 완료/취소
 */
@Tag(name = "Consultation Admin", description = "관리자용 상담 신청 관리 API (목록 조회, 완료/취소)")
@RestController
@RequestMapping("/api/admin/consultations")
@RequiredArgsConstructor
public class AdminConsultationController {

    private final MemberRepository memberRepository;
    private final ConsultationService consultationService;

    /**
     * GET /api/admin/consultations 현재 로그인한 관리자 회사의 상담 신청 전체 목록 (대기/진행중/완료/취소
     * 모두)
     */
    @Operation(summary = "상담 신청 목록 조회", description = "관리자 소속 회사의 모든 상담 신청 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<ConsultationDto.ConsultationListItemRes>> getConsultations(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (member.getCompany() == null) {
            return ResponseEntity.ok(Page.empty());
        }
        Page<ConsultationDto.ConsultationListItemRes> list = consultationService.getConsultationListByCompany(
                member.getCompany().getCompanyId(), PageRequest.of(page, size));
        return ResponseEntity.ok(list);
    }

    /**
     * PUT /api/admin/consultations/{consultationId}/complete 상담 완료 처리 (승인과 동일)
     */
    @Operation(summary = "상담 완료 처리", description = "특정 상담 신청을 완료 상태로 변경합니다.")
    @PutMapping("/{consultationId}/complete")
    public ResponseEntity<Void> completeConsultation(
            @Parameter(description = "상담 ID", example = "7") @PathVariable Long consultationId) {
        try {
            consultationService.completeConsultation(consultationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/admin/consultations/{consultationId}/cancel 상담 취소 처리 (반려와 동일)
     */
    @Operation(summary = "상담 취소 처리", description = "특정 상담 신청을 취소 상태로 변경합니다.")
    @PutMapping("/{consultationId}/cancel")
    public ResponseEntity<Void> cancelConsultation(
            @Parameter(description = "상담 ID", example = "7") @PathVariable Long consultationId) {
        try {
            consultationService.cancelConsultation(consultationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
