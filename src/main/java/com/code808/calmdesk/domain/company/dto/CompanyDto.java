package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.entity.Company;
import com.code808.calmdesk.domain.member.entity.Department;
import com.code808.calmdesk.domain.common.enums.CommonEnums;

import jakarta.validation.constraints.*;
import lombok.*;


public class CompanyDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CodeResponse {
        private String companyCode;

        public static CodeResponse of(String companyCode) {
            return CodeResponse.builder()
                    .companyCode(companyCode)
                    .build();
            }
        }
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class RegisterRequest {

            private String companyCode;

            @NotBlank(message = "회사명은 필수입니다")
            @Size(min = 2, max = 50, message = "회사명은 2자 이상 50자 이하여야 합니다")
            private String companyName;

            @NotBlank(message = "업종은 필수입니다")
            @Size(max = 50, message = "업종은 50자 이하여야 합니다")
            private String category;

            @NotNull(message = "최소 금액은 필수입니다")
            @Min(value = 0, message = "최소 금액은 0 이상이어야 합니다")
            private Integer minValue;

            @NotNull(message = "최대 금액은 필수입니다")
            @Min(value = 0, message = "최대 금액은 0 이상이어야 합니다")
            private Integer maxValue;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class RegisterResponse {
            private Long companyId;
            private String companyCode;
            private String companyName;
            private String category;
            private Integer minValue;
            private Integer maxValue;

            public static RegisterResponse of(Company company, Member member) {
                return RegisterResponse.builder()
                        .companyId(company.getCompanyId())
                        .companyCode(company.getCompanyCode())
                        .companyName(company.getCompanyName())
                        .category(company.getCategory())
                        .minValue(company.getMinValue())
                        .maxValue(company.getMaxValue())
                        .build();
            }
        }
}
