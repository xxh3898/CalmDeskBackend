package com.code808.calmdesk.domain.businesscard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 명함 등록 시 멤버(입사 신청)에 맞는 값만 사용. 이름, 연락처, 이메일, 부서, 직급만 등록되며 항상 직원 입사 신청으로 생성됨.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCardRegisterRequest {

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "연락처", example = "010-1234-5678")
    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;

    @Schema(description = "이메일", example = "hong@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "부서 ID", example = "5")
    @NotNull(message = "부서는 필수입니다.")
    private Long departmentId;

    @Schema(description = "직급 ID", example = "2")
    @NotNull(message = "직급은 필수입니다.")
    private Long rankId;
}
