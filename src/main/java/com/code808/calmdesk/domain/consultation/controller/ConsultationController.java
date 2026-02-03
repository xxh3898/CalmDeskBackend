package com.code808.calmdesk.domain.consultation.controller;

import com.code808.calmdesk.domain.consultation.dto.ConsultationCreateRequest;
import com.code808.calmdesk.domain.consultation.dto.ConsultationDto;
import com.code808.calmdesk.domain.consultation.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<Void> createConsultation(
            @RequestBody @Valid ConsultationCreateRequest request,
            java.security.Principal principal) {
        Long consultationId = consultationService.createConsultation(request, principal.getName());
        return ResponseEntity.created(URI.create("/api/consultations/" + consultationId)).build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getWaitingCount() {
        long count = consultationService.getWaitingCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/consultations/me
     * 직원 본인 상담 신청 목록 (근태 캘린더 등에서 사용)
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
