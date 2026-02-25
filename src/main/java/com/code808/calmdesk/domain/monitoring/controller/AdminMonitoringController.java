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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Monitoring Admin", description = "관리자용 실시간 모니터링 API")
@RestController
@RequestMapping("/api/admin/monitoring")
@RequiredArgsConstructor
public class AdminMonitoringController {

    private final com.code808.calmdesk.domain.member.repository.MemberRepository memberRepository;
    private final AdminMonitoringService adminMonitoringService;

    @Operation(summary = "모니터링 데이터 조회", description = "현재 또는 특정 연도의 실시간 출근 지표(정상 출근, 지각, 미출근 등)를 조회합니다.")
    @GetMapping
    public ResponseEntity<MonitoringDto> getMonitoringData(
            @Parameter(description = "조회 기간 (current, yearly)", example = "current") @RequestParam(required = false, defaultValue = "current") String period,
            @Parameter(description = "조회 연도 (period가 yearly인 경우 필수)", example = "2026") @RequestParam(required = false) Integer year,
            java.security.Principal principal
    ) {
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
