package com.code808.calmdesk.domain.consultation.controller.admin;

import com.code808.calmdesk.domain.consultation.dto.ConsultationDto;
import com.code808.calmdesk.domain.consultation.service.ConsultationService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 관리자용 상담 신청 조회 및 처리 API (휴가/입사 신청과 동일한 흐름)
 * GET 목록(회사별), PUT 완료/취소
 */
@RestController
@RequestMapping("/api/admin/consultations")
@RequiredArgsConstructor
public class AdminConsultationController {

    private final MemberRepository memberRepository;
    private final ConsultationService consultationService;

    /**
     * GET /api/admin/consultations
     * 현재 로그인한 관리자 회사의 상담 신청 전체 목록 (대기/진행중/완료/취소 모두)
     */
    @GetMapping
    public ResponseEntity<List<ConsultationDto.ConsultationListItemRes>> getConsultations(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (member.getCompany() == null) {
            return ResponseEntity.ok(List.of());
        }
        List<ConsultationDto.ConsultationListItemRes> list = consultationService.getConsultationListByCompany(member.getCompany().getCompanyId());
        return ResponseEntity.ok(list);
    }

    /**
     * PUT /api/admin/consultations/{consultationId}/complete
     * 상담 완료 처리 (승인과 동일)
     */
    @PutMapping("/{consultationId}/complete")
    public ResponseEntity<Void> completeConsultation(@PathVariable Long consultationId) {
        try {
            consultationService.completeConsultation(consultationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/admin/consultations/{consultationId}/cancel
     * 상담 취소 처리 (반려와 동일)
     */
    @PutMapping("/{consultationId}/cancel")
    public ResponseEntity<Void> cancelConsultation(@PathVariable Long consultationId) {
        try {
            consultationService.cancelConsultation(consultationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
