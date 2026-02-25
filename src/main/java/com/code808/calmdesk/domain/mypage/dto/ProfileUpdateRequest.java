package com.code808.calmdesk.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Schema(description = "이메일", example = "hong_new@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "전화번호", example = "010-9999-8888")
    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;

    @Schema(description = "입사일 (yyyy.MM.dd)", example = "2024.01.01")
    /**
     * 입사일 (yyyy-MM-dd 또는 yyyy.MM.dd, 선택)
     */
    private String joinDate;
}
