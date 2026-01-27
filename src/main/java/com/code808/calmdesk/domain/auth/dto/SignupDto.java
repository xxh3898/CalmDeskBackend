package com.code808.calmdesk.domain.auth.dto;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.entity.Company;
import com.code808.calmdesk.domain.member.entity.Department;
import com.code808.calmdesk.domain.member.entity.Rank;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

public class SignupDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignupRequest{
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
        )
        private String password;

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다")
        private String name;

        private String phone;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignupResponse{
        private Long memberId;
        private String email;
        private String name;
        private String phone;
        private Member.Role role;
        private CommonEnums.Status active;
//        private String hireDate;
        private Company company;
        private Department department;
        private Rank rank;
        private boolean requiresCompanySetup;
        private String token;

        public static SignupResponse of(Member member, String token){
            return SignupResponse.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .active(member.getStatus())
//                    .hireDate(member.getHireDate())
                    .requiresCompanySetup(true)
                    .token(token)
                    .build();
        }
    }
}