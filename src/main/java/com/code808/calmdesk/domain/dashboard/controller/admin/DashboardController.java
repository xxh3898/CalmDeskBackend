package com.code808.calmdesk.domain.dashboard.controller.admin;

import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.repository.admin.DashboardRepository;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import com.code808.calmdesk.domain.dashboard.sse.SseEmitterRegistry;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
//    private final DashboardRepository dashboardRepository;
    private final MemberRepository memberRepository;
    private final SseEmitterRegistry sseEmitterRegistry;

//    @GetMapping("/stats/department")
//    public ResponseEntity<List<DashboardDto.DepartmentStats>> getDepartmentStats(
//            @Valid @ModelAttribute DashboardDto.DashboardRequest request) {
//        return ResponseEntity.ok(dashboardService.getDepartmentStats(request));
//    }
//
//    @GetMapping("/high-risk-users")
//    public ResponseEntity<List<DashboardDto.HighRiskMember>> getHighRiskMembers(
//            @Valid @ModelAttribute DashboardDto.DashboardRequest request) {
//        return ResponseEntity.ok(dashboardService.getHighRiskMembers(request));
//    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardDto.DashboardResponse> getDashboardStats(
            @Valid @ModelAttribute DashboardDto.DashboardRequest request,
            Principal principal) {
        setCompanyId(request, principal);
        return ResponseEntity.ok(dashboardService.getAllStats(request));
    }

    private void setCompanyId(DashboardDto.DashboardRequest request, Principal principal) {
        Long companyId = memberRepository.findCompanyIdByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("해당 이메일을 가진 멤버의 회사 정보를 찾을 수 없습니다."));
        request.setCompanyId(companyId);
    }

    @GetMapping("/stats/yesterday")
    public ResponseEntity<DashboardDto.DashboardResponse> getYesterdayStats(
            @Valid @ModelAttribute DashboardDto.DashboardRequest request,
            Principal principal) {
        setCompanyId(request, principal);
        request.setDate(LocalDate.now().minusDays(1));
        return ResponseEntity.ok(dashboardService.getAllStats(request));
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Principal principal) {
        Long companyId = memberRepository.findCompanyIdByEmail(principal.getName())
                        .orElseThrow(()->new RuntimeException("해당 이메일을 가진 멤버의 회사 정보를 찾을 수 없습니다."));
        log.info("SSE 구독 요청 - companyId: {}", companyId);
        return sseEmitterRegistry.register(companyId);
    }

}
