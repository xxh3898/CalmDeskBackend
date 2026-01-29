package com.code808.calmdesk.domain.auth.dto;

import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
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
        private CommonEnums.Status joinStatus;
        private String companyName;
        private String departmentName;
        private String rankName;
        private String companyCode;
        private String token;

        public static LoginResponse of(Member member, String token) {
            return LoginResponse.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .joinStatus(member.getStatus())
                    .companyCode(member.getCompany().getCompanyCode())
                    .companyName(member.getCompany().getCompanyName())
                    .departmentName(member.getDepartment().getDepartmentName())
                    .rankName(member.getRank().getRankName())
                    .token(token)
                    .build();
        }
    }
}