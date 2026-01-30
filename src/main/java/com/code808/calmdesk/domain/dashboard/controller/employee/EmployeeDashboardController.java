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
        Member member;
        // TODO: 배포 시 제거 - 테스트용 하드코딩
        if (principal == null) {
            member = memberRepository.findById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        } else {
            String email = principal.getName();
            member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        }

        EmployeeDashboardResponseDto data = dashboardService.getDashboardData(member.getMemberId());
        return ResponseEntity.ok(data);
    }
}
