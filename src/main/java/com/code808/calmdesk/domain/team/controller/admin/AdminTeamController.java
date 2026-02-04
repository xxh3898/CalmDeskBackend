package com.code808.calmdesk.domain.team.controller.admin;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import com.code808.calmdesk.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/team")
@RequiredArgsConstructor
public class AdminTeamController {

    private final TeamService teamService;
    private final MemberRepository memberRepository;

    @GetMapping("/members")
    public ResponseEntity<List<TeamMemberResponse>> getMembers(Principal principal) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(List.of());
        }
        List<TeamMemberResponse> members = teamService.getMembersByCompanyId(admin.getCompany().getCompanyId());
        return ResponseEntity.ok(members);
    }

    @GetMapping("/members/{memberId}/attendance")
    public ResponseEntity<Map<String, String>> getMemberAttendance(
            Principal principal,
            @PathVariable Long memberId,
            @RequestParam int year,
            @RequestParam int month) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(Map.of());
        }
        Map<String, String> attendance = teamService.getMemberAttendanceByMonth(
                memberId, admin.getCompany().getCompanyId(), year, month);
        return ResponseEntity.ok(attendance);
    }
}
