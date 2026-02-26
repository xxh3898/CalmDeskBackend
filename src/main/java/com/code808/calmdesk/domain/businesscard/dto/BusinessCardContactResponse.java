package com.code808.calmdesk.domain.businesscard.dto;

import com.code808.calmdesk.domain.businesscard.entity.BusinessCardContact;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 명함 연락처 API 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "명함 연락처 응답")
public class BusinessCardContactResponse {

    @Schema(description = "ID", example = "1")
    private Long id;
    @Schema(description = "연락처 타입 (EMPLOYEE, EXTERNAL, PARTNER 등)", example = "EXTERNAL")
    private String contactType;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "회사명", example = "코드808")
    private String companyName;
    @Schema(description = "직책", example = "팀장")
    private String title;
    @Schema(description = "전화번호", example = "02-123-4567")
    private String phone;
    @Schema(description = "휴대폰 번호", example = "010-1234-5678")
    private String mobile;
    @Schema(description = "이메일", example = "hong@example.com")
    private String email;
    @Schema(description = "주소", example = "서울시 강남구...")
    private String address;
    @Schema(description = "부서 ID", example = "5")
    private Long departmentId;
    @Schema(description = "부서명", example = "개발팀")
    private String departmentName;

    public static BusinessCardContactResponse from(BusinessCardContact c) {
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
}
