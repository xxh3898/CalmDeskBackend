package com.code808.calmdesk.domain.dashboard.controller.employee;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.service.employee.EmployeeDashboardService;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee/dashboard")
@RequiredArgsConstructor
public class EmployeeDashboardController {

    private final EmployeeDashboardService dashboardService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<EmployeeDashboardResponseDto> getDashboard(Principal principal) {
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        EmployeeDashboardResponseDto data = dashboardService.getDashboardData(member.getMemberId());
        return ResponseEntity.ok(data);
    }
}
