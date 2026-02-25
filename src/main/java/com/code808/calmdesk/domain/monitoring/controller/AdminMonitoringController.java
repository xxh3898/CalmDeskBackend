package com.code808.calmdesk.domain.monitoring.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
            java.security.Principal principal) {
        Long companyId = memberRepository.findCompanyIdByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok(adminMonitoringService.getMonitoringData(period, year, companyId));
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam(required = false, defaultValue = "current") String period,
            @RequestParam(required = false) Integer year,
            java.security.Principal principal) {
        Long companyId = memberRepository.findCompanyIdByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        byte[] excelData = adminMonitoringService.generateExcelReport(period, year, companyId);

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        String filename = URLEncoder.encode(
                "모니터링_보고서_" + targetYear + "_" + period + ".xlsx",
                StandardCharsets.UTF_8).replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
