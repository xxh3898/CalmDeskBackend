package com.code808.calmdesk.domain.auth.dto;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Rank;

import io.swagger.v3.oas.annotations.media.Schema;
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
    public static class SignupRequest {

        @Schema(description = "이메일 주소", example = "user@example.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8~20자)", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
        )
        private String password;

        @Schema(description = "이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다")
        private String name;

        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignupResponse {

        @Schema(description = "사용자 ID", example = "1")
        private Long memberId;
        @Schema(description = "이메일 주소", example = "user@example.com")
        private String email;
        @Schema(description = "이름", example = "홍길동")
        private String name;
        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;
        @Schema(description = "권한", example = "USER")
        private Member.Role role;
        @Schema(description = "상태", example = "ACTIVE")
        private CommonEnums.Status active;
//        private String hireDate;
        @Schema(description = "회사 정보")
        private Company company;
        @Schema(description = "부서 정보")
        private Department department;
        @Schema(description = "직급 정보")
        private Rank rank;
        @Schema(description = "회사 설정 필요 여부", example = "true")
        private boolean requiresCompanySetup;
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String accessToken;
        @Builder.Default
        @Schema(description = "토큰 타입", example = "Bearer")
        private String tokenType = "Bearer";

        public static SignupResponse of(Member member, String accessToken) {
            return SignupResponse.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .phone(member.getPhone())
                    .role(member.getRole())
                    .active(member.getStatus())
                    //                    .hireDate(member.getHireDate())
                    .requiresCompanySetup(true)
                    .accessToken(accessToken)
                    .build();
        }
    }
}
