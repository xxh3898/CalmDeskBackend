package com.code808.calmdesk.domain.auth.dto;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LoginDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {

        @Schema(description = "이메일 주소", example = "user1@test.com")
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;
        @Schema(description = "비밀번호", example = "pass1234!")
        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {

        @Schema(description = "사용자 ID", example = "1")
        private Long memberId;
        @Schema(description = "이메일 주소", example = "user@example.com")
        private String email;
        @Schema(description = "이름", example = "홍길동")
        private String name;
        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;
        @Schema(description = "사용자 권한", example = "ADMIN")
        private Member.Role role;
        @Schema(description = "회사 ID", example = "5")
        private Long companyId;
        @Schema(description = "가입 상태", example = "ACTIVE")
        private CommonEnums.Status joinStatus;
        @Schema(description = "회사명", example = "코드808")
        private String companyName;
        @Schema(description = "부서명", example = "개발팀")
        private String departmentName;
        @Schema(description = "직급명", example = "대리")
        private String rankName;
        @Schema(description = "회사 코드", example = "C12345")
        private String companyCode;
        @Schema(description = "부서 ID", example = "5")
        private Long departmentId;
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String accessToken;
//        private String refreshToken;
        @Builder.Default
        @Schema(description = "토큰 타입", example = "Bearer")
        private String tokenType = "Bearer";

        public static LoginResponse of(Member member, String accessToken) {
            return LoginResponse.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .companyId(member.getCompany() != null ? member.getCompany().getCompanyId() : null) // ✨ 핵심!
                    .joinStatus(member.getStatus())
                    .companyCode(member.getCompany().getCompanyCode())
                    .companyName(member.getCompany().getCompanyName())
                    .departmentName(member.getDepartment().getDepartmentName())
                    .departmentId(member.getDepartment().getDepartmentId())
                    .rankName(member.getRank().getRankName())
                    .accessToken(accessToken)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AuthContext {

        private Member member;
        private String accessToken;
        private String refreshToken;
    }

}
