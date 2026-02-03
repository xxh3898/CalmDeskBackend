package com.code808.calmdesk.domain.consultation.controller;

import java.net.URI;
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

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getWaitingCount() {
        long count = consultationService.getWaitingCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
