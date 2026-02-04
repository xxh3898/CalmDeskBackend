package com.code808.calmdesk.domain.dashboard.controller.admin;

import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats/department")
    public ResponseEntity<List<DashboardDto.DepartmentStats>> getDepartmentStats(
           @Valid @ModelAttribute DashboardDto.DashboardRequest request) {
        return ResponseEntity.ok(dashboardService.getDepartmentStats(request));
    }

    @GetMapping("/high-risk-users")
    public ResponseEntity<List<DashboardDto.HighRiskMember>> getHighRiskMembers(
            @Valid @ModelAttribute DashboardDto.DashboardRequest request){
        return ResponseEntity.ok(dashboardService.getHighRiskMembers(request));
    }

}
