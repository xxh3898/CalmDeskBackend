package com.example.demo.dto.mypage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;
}
