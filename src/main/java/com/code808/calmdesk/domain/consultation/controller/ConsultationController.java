package com.code808.calmdesk.domain.consultation.controller;

import com.code808.calmdesk.domain.consultation.dto.ConsultationCreateRequest;
import com.code808.calmdesk.domain.consultation.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
}
