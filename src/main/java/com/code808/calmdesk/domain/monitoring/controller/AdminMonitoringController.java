package com.code808.calmdesk.domain.monitoring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;
import com.code808.calmdesk.domain.monitoring.service.AdminMonitoringService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/monitoring")
@RequiredArgsConstructor
public class AdminMonitoringController {

    private final com.code808.calmdesk.domain.member.repository.MemberRepository memberRepository;
    private final AdminMonitoringService adminMonitoringService;

    @GetMapping
    public ResponseEntity<MonitoringDto> getMonitoringData(
            @RequestParam(required = false, defaultValue = "current") String period,
            @RequestParam(required = false) Integer year,
            java.security.Principal principal
    ) {
        Long companyId = memberRepository.findCompanyIdByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok(adminMonitoringService.getMonitoringData(period, year, companyId));
    }
}
