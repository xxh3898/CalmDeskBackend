package com.code808.calmdesk.domain.dashboard.controller.employee;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.dashboard.dto.employee.DashboardStatusUpdateReq;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.service.employee.EmployeeDashboardService;
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
        Long memberId = getMemberId(principal);
        EmployeeDashboardResponseDto data = dashboardService.getDashboardData(memberId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/status/clock-in")
    public ResponseEntity<Void> clockIn(Principal principal) {
        Long memberId = getMemberId(principal);
        dashboardService.clockIn(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/status/clock-out")
    public ResponseEntity<Void> clockOut(Principal principal) {
        Long memberId = getMemberId(principal);
        dashboardService.clockOut(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/status")
    public ResponseEntity<Void> updateStatus(Principal principal, @RequestBody DashboardStatusUpdateReq req) {
        Long memberId = getMemberId(principal);
        dashboardService.updateStatus(memberId, req.getStatus());
        return ResponseEntity.ok().build();
    }

    private Long getMemberId(Principal principal) {
        // TODO: 배포 시 제거 - 테스트용 하드코딩 (principal null일 경우 1L)
        if (principal == null) {
            return 1L;
        }
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                .getMemberId();
    }
}
