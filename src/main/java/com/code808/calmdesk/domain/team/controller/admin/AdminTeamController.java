package com.code808.calmdesk.domain.team.controller.admin;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import com.code808.calmdesk.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<Page<TeamMemberResponse>> getMembers(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(Page.empty());
        }
        Page<TeamMemberResponse> members = teamService.getMembersByCompanyId(
                admin.getCompany().getCompanyId(), PageRequest.of(page, size));
        return ResponseEntity.ok(members);
    }

    @GetMapping("/stats")
    public ResponseEntity<TeamService.TeamStats> getTeamStats(Principal principal) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(new TeamService.TeamStats(0L, 0L, 0L));
        }
        return ResponseEntity.ok(teamService.getTeamStats(admin.getCompany().getCompanyId()));
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

    @GetMapping("/departments")
    public ResponseEntity<List<String>> getDepartments(Principal principal) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(List.of());
        }
        List<String> names = teamService.getDepartmentNamesByCompanyId(admin.getCompany().getCompanyId());
        return ResponseEntity.ok(names);
    }

    /** 명함 등록 시 팀(부서) 선택용 - departmentId, departmentName 반환 */
    @GetMapping("/departments-list")
    public ResponseEntity<List<TeamService.DepartmentItem>> getDepartmentsList(Principal principal) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(teamService.getDepartmentsByCompanyId(admin.getCompany().getCompanyId()));
    }

    @PostMapping("/departments")
    public ResponseEntity<Void> createDepartment(Principal principal, @RequestBody CreateDepartmentRequest request) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.badRequest().build();
        }
        teamService.createDepartment(admin.getCompany().getCompanyId(), request.getDepartmentName());
        return ResponseEntity.ok().build();
    }

    @lombok.Getter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateDepartmentRequest {
        private String departmentName;
    }
}
