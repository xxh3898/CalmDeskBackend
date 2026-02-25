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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Tag(name = "Team Admin", description = "관리자용 팀/멤버 관리 API")
@RestController
@RequestMapping("/api/admin/team")
@RequiredArgsConstructor
public class AdminTeamController {

    private final TeamService teamService;
    private final MemberRepository memberRepository;

    @Operation(summary = "전체 멤버 목록 조회 (페이징)", description = "관리자 소속 회사의 모든 멤버 목록을 페이징하여 조회합니다.")
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

    @Operation(summary = "팀 통계 조회", description = "회사 내 전체 멤버 수, 오늘 출근자 수, 위험군 수 등의 통계를 조회합니다.")
    @GetMapping("/stats")
    public ResponseEntity<TeamService.TeamStats> getTeamStats(Principal principal) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(new TeamService.TeamStats(0L, 0L, 0L));
        }
        return ResponseEntity.ok(teamService.getTeamStats(admin.getCompany().getCompanyId()));
    }

    @Operation(summary = "특정 멤버 근태 조회", description = "특정 멤버의 월별 근태 상태 목록을 조회합니다.")
    @GetMapping("/members/{memberId}/attendance")
    public ResponseEntity<Map<String, String>> getMemberAttendance(
            Principal principal,
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId,
            @Parameter(description = "연도", example = "2026") @RequestParam int year,
            @Parameter(description = "월", example = "3") @RequestParam int month) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(Map.of());
        }
        Map<String, String> attendance = teamService.getMemberAttendanceByMonth(
                memberId, admin.getCompany().getCompanyId(), year, month);
        return ResponseEntity.ok(attendance);
    }

    @Operation(summary = "부서 목록 조회 (이름만)", description = "관리자 소속 회사의 모든 부서 이름을 조회합니다.")
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

    /**
     * 명함 등록 시 팀(부서) 선택용 - departmentId, departmentName 반환
     */
    @Operation(summary = "부서 목록 상세 조회", description = "부서 ID와 이름을 포함한 부서 목록을 조회합니다.")
    @GetMapping("/departments-list")
    public ResponseEntity<List<TeamService.DepartmentItem>> getDepartmentsList(Principal principal) {
        Member admin = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (admin.getCompany() == null || admin.getCompany().getCompanyId() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(teamService.getDepartmentsByCompanyId(admin.getCompany().getCompanyId()));
    }

    @Operation(summary = "부서 생성", description = "새로운 부서를 생성합니다.")
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
