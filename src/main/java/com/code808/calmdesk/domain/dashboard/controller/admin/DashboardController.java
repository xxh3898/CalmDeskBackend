package com.code808.calmdesk.domain.dashboard.controller.admin;

import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.repository.admin.DashboardRepository;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    private final DashboardRepository dashboardRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/stats/department")
    public ResponseEntity<List<DashboardDto.DepartmentStats>> getDepartmentStats(
            @Valid @ModelAttribute DashboardDto.DashboardRequest request) {
        return ResponseEntity.ok(dashboardService.getDepartmentStats(request));
    }

    @GetMapping("/high-risk-users")
    public ResponseEntity<List<DashboardDto.HighRiskMember>> getHighRiskMembers(
            @Valid @ModelAttribute DashboardDto.DashboardRequest request) {
        return ResponseEntity.ok(dashboardService.getHighRiskMembers(request));
    }

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
}
