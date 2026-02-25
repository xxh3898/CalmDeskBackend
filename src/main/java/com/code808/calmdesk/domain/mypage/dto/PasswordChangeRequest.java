package com.code808.calmdesk.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @Schema(description = "현재 비밀번호", example = "old_password123")
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "new_password!@#")
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}
