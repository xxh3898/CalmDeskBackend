package com.code808.calmdesk.domain.company.controller;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final MemberRepository memberRepository;

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentDto.DetailResponse> getDepartmentDetails(
            @PathVariable Long departmentId,
            Authentication authentication) {
        Long companyId = getCurrentUserCompanyId(authentication);
        DepartmentDto.DetailResponse departmentInfo = departmentService.getDepartmentDetailsByCompany(departmentId, companyId);
        return ResponseEntity.ok(departmentInfo);
    }

    @GetMapping("/{departmentId}/members")
    public ResponseEntity<org.springframework.data.domain.Page<DepartmentDto.MemberResponse>> getDepartmentMembers(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication) {
        Long companyId = getCurrentUserCompanyId(authentication);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<DepartmentDto.MemberResponse> members = departmentService.getDepartmentMembersByCompany(departmentId, companyId, pageable);
        return ResponseEntity.ok(members);
    }

    /** 로그인 사용자의 회사 ID 조회 (없으면 예외) */
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
