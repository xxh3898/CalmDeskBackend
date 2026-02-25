package com.code808.calmdesk.domain.company.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;
import com.code808.calmdesk.domain.company.service.DepartmentService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Department", description = "부서 정보 및 부서원 관리 API")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final MemberRepository memberRepository;

    @Operation(summary = "부서 상세 정보 조회", description = "특정 부서의 기본 정보와 소속 인원 수를 조회합니다.")
    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentDto.DetailResponse> getDepartmentDetails(
            @Parameter(description = "부서 ID", example = "2") @PathVariable Long departmentId,
            Authentication authentication) {
        Long companyId = getCurrentUserCompanyId(authentication);
        DepartmentDto.DetailResponse departmentInfo = departmentService.getDepartmentDetailsByCompany(departmentId, companyId);
        return ResponseEntity.ok(departmentInfo);
    }

    @Operation(summary = "부서원 목록 조회 (페이징)", description = "특정 부서에 소속된 직원 목록을 페이징하여 조회합니다.")
    @GetMapping("/{departmentId}/members")
    public ResponseEntity<Page<DepartmentDto.MemberResponse>> getDepartmentMembers(
            @Parameter(description = "부서 ID", example = "2") @PathVariable Long departmentId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "5") int size,
            Authentication authentication) {
        Long companyId = getCurrentUserCompanyId(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<DepartmentDto.MemberResponse> members = departmentService.getDepartmentMembersByCompany(departmentId, companyId, pageable);
        return ResponseEntity.ok(members);
    }

    /**
     * 로그인 사용자의 회사 ID 조회 (없으면 예외)
     */
    private Long getCurrentUserCompanyId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("인증이 필요합니다.");
        }
        return memberRepository.findEmailWithDetails(authentication.getName())
                .filter(m -> m.getCompany() != null && m.getCompany().getCompanyId() != null)
                .map(m -> m.getCompany().getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("회사에 소속된 사용자만 부서 정보를 조회할 수 있습니다."));
    }
}
