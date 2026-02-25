package com.code808.calmdesk.domain.businesscard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 명함 등록 시 멤버(입사 신청)에 맞는 값만 사용.
 * 이름, 연락처, 이메일, 부서, 직급만 등록되며 항상 직원 입사 신청으로 생성됨.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCardRegisterRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "부서는 필수입니다.")
    private Long departmentId;

    @NotNull(message = "직급은 필수입니다.")
    private Long rankId;
}
