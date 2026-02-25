package com.code808.calmdesk.domain.businesscard.controller;

import com.code808.calmdesk.domain.businesscard.dto.BusinessCardExtractedDto;
import com.code808.calmdesk.domain.businesscard.dto.BusinessCardRegisterRequest;
import com.code808.calmdesk.domain.businesscard.entity.BusinessCardContact;
import com.code808.calmdesk.domain.businesscard.service.BusinessCardService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 명함 이미지 인식 → 자동 등록 (직원/외부인/협력사).
 * - POST /api/business-card/extract: 이미지 업로드 → AI 추출 결과 반환
 * - POST /api/business-card/register: 추출 결과 + 팀 선택 → 엔티티 등록
 * - GET /api/business-card/contacts: 회사별 명함 연락처 목록
 */
@RestController
@RequestMapping("/api/business-card")
@RequiredArgsConstructor
public class BusinessCardController {

    private final BusinessCardService businessCardService;
    private final MemberRepository memberRepository;

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BusinessCardExtractedDto>> extract(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("명함 이미지 파일을 선택해 주세요."));
        }
        String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
        try {
            BusinessCardExtractedDto result = businessCardService.extractFromImage(file.getBytes(), contentType);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(BusinessCardExtractedDto.builder()
                    .extractionError("파일 처리 실패: " + e.getMessage())
                    .build()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<BusinessCardContactResponse>> register(
            @Valid @RequestBody BusinessCardRegisterRequest request,
            Principal principal) {
        BusinessCardContact contact = businessCardService.register(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(toResponse(contact)));
    }

    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<List<BusinessCardContactResponse>>> listContacts(Principal principal) {
        var member = memberRepository.findEmailWithDetails(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
        if (member.getCompany() == null) {
            return ResponseEntity.ok(ApiResponse.success(List.of()));
        }
        List<BusinessCardContact> list = businessCardService.listByCompany(member.getCompany().getCompanyId());
        return ResponseEntity.ok(ApiResponse.success(list.stream().map(this::toResponse).collect(Collectors.toList())));
    }

    private BusinessCardContactResponse toResponse(BusinessCardContact c) {
        return BusinessCardContactResponse.builder()
                .id(c.getId())
                .contactType(c.getContactType().name())
                .name(c.getName())
                .companyName(c.getCompanyName())
                .title(c.getTitle())
                .phone(c.getPhone())
                .mobile(c.getMobile())
                .email(c.getEmail())
                .address(c.getAddress())
                .departmentId(c.getDepartment() != null ? c.getDepartment().getDepartmentId() : null)
                .departmentName(c.getDepartment() != null ? c.getDepartment().getDepartmentName() : null)
                .build();
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BusinessCardContactResponse {
        private Long id;
        private String contactType;
        private String name;
        private String companyName;
        private String title;
        private String phone;
        private String mobile;
        private String email;
        private String address;
        private Long departmentId;
        private String departmentName;
    }
}
