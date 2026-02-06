package com.code808.calmdesk.domain.consultation.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.consultation.dto.ConsultationDto;
import com.code808.calmdesk.domain.consultation.service.ConsultationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<Void> createConsultation(
            @RequestBody @Valid ConsultationDto.ConsultationCreateRequest request,
            java.security.Principal principal) {
        Long consultationId = consultationService.createConsultation(request, principal.getName());
        return ResponseEntity.created(URI.create("/api/consultations/" + consultationId)).build();
    }

    private final com.code808.calmdesk.domain.member.repository.MemberRepository memberRepository;

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getWaitingCount(java.security.Principal principal) {
        Long companyId = memberRepository.findCompanyIdByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        long count = consultationService.getWaitingCount(companyId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/consultations/me 직원 본인 상담 신청 목록 (근태 캘린더 등에서 사용)
     */
    @GetMapping("/me")
    public ResponseEntity<List<ConsultationDto.ConsultationListItemRes>> getMyConsultations(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(List.of());
        }
        List<ConsultationDto.ConsultationListItemRes> list = consultationService.getMyConsultationList(principal.getName());
        return ResponseEntity.ok(list);
    }
}
