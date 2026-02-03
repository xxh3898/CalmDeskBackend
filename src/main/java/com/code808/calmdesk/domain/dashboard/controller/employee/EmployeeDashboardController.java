package com.code808.calmdesk.domain.dashboard.controller.employee;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.dashboard.dto.employee.DashboardStatusUpdateReq;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmotionCheckInRequest;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.service.employee.EmployeeDashboardService;
import com.code808.calmdesk.domain.gifticon.service.ShopEmployeeService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee/dashboard")
@RequiredArgsConstructor
public class EmployeeDashboardController {

    private final EmployeeDashboardService dashboardService;
    private final MemberRepository memberRepository;
    private final ShopEmployeeService shopEmployeeService;

    @GetMapping
    public ResponseEntity<EmployeeDashboardResponseDto> getDashboard(Principal principal) {
        Long memberId = getMemberId(principal);
        EmployeeDashboardResponseDto data = dashboardService.getDashboardData(memberId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/status/clock-in")
    public ResponseEntity<Void> clockIn(Principal principal, @RequestBody EmotionCheckInRequest req) {
        Long memberId = getMemberId(principal);
        dashboardService.clockIn(memberId, req);

        // 1. 진행도를 먼저 업데이트해서 목표치(1/1)를 채웁니다.
        // 1. 매일 출근 미션 (단발성 혹은 덮어쓰기라면 false, 누적이라면 true)
        // 출근 미션 코드가 "ATT_DAILY"라고 가정
        shopEmployeeService.updateMissionProgress(memberId, "ATT_DAILY", 1, false);
        shopEmployeeService.updateMissionProgress(memberId, "ATT_RATE_80", 1, true);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/status/clock-out")
    public ResponseEntity<Void> clockOut(Principal principal, @RequestBody EmotionCheckInRequest req) {
        Long memberId = getMemberId(principal);
        dashboardService.clockOut(memberId, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/status")
    public ResponseEntity<Void> updateStatus(Principal principal, @RequestBody DashboardStatusUpdateReq req) {
        Long memberId = getMemberId(principal);
        dashboardService.updateStatus(memberId, req.getStatus());
        return ResponseEntity.ok().build();
    }

    private Long getMemberId(Principal principal) {
        return memberRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                .getMemberId();
    }
}
