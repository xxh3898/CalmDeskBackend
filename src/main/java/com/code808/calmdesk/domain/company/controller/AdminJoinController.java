package com.code808.calmdesk.domain.company.controller;

import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.service.CompanyService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 관리자용 입사 신청 조회 및 처리 API
 * GET 목록, PUT 승인/반려
 */
@RestController
@RequestMapping("/api/admin/joins")
@RequiredArgsConstructor
public class AdminJoinController {

    private final MemberRepository memberRepository;
    private final CompanyService companyService;

    /**
     * GET /api/admin/joins
     * 현재 로그인한 관리자 회사의 입사 신청 전체 목록 (대기/승인/반려 모두, 휴가 탭처럼 유지)
     */
    @GetMapping
    public ResponseEntity<List<CompanyDto.JoinListItemRes>> getJoins(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        if (member.getCompany() == null) {
            return ResponseEntity.ok(List.of());
        }
        List<CompanyDto.JoinListItemRes> list = companyService.listAllJoins(member.getCompany().getCompanyId());
        return ResponseEntity.ok(list);
    }

    /**
     * PUT /api/admin/joins/{memberId}/approve
     * 입사 신청 승인
     */
    @PutMapping("/{memberId}/approve")
    public ResponseEntity<Void> approveJoin(@PathVariable Long memberId, Principal principal) {
        companyService.approveJoin(memberId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/admin/joins/{memberId}/reject
     * 입사 신청 반려
     */
    @PutMapping("/{memberId}/reject")
    public ResponseEntity<Void> rejectJoin(@PathVariable Long memberId, Principal principal) {
        companyService.rejectJoin(memberId, principal.getName());
        return ResponseEntity.ok().build();
    }
}
