package com.code808.calmdesk.domain.auth.dto;

import com.code808.calmdesk.domain.member.entity.Company;
import com.code808.calmdesk.domain.member.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.entity.Rank;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class LoginDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private Long memberId;
        private String email;
        private String name;
        private String phone;
        private Member.Role role;
        private CommonEnums.Status status;
        private Company company;
        private Department department;
        private Rank rank;
        private String token;

        public static LoginResponse of(Member member, String token) {
            return LoginResponse.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .status(member.getStatus())
                    .company(member.getCompany())
                    .department(member.getDepartment())
                    .rank(member.getRank())
                    .token(token)
                    .build();
        }
    }
}