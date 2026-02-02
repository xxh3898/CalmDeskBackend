package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Rank;
import com.code808.calmdesk.domain.common.enums.CommonEnums;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;


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
            private String token;

            public static RegisterResponse of(Company company, Member member, String token) {
                return RegisterResponse.builder()
                        .companyId(company.getCompanyId())
                        .companyCode(company.getCompanyCode())
                        .companyName(company.getCompanyName())
                        .category(company.getCategory())
                        .minValue(company.getMinValue())
                        .maxValue(company.getMaxValue())
                        .token(token)
                        .build();
            }
        }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentInfo {
        private Long departmentId;
        private String departmentName;

        public static DepartmentInfo of(Department department) {
            return DepartmentInfo.builder()
                    .departmentId(department.getDepartmentId())
                    .departmentName(department.getDepartmentName())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RankInfo {
        private Long rankId;
        private String rankName;

        public static RankInfo of(Rank rank) {
            return RankInfo.builder()
                    .rankId(rank.getRankId())
                    .rankName(rank.getRankName())
                    .build();
        }
    }
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class CheckResponse {
            private Long companyId;
            private String companyCode;
            private String companyName;
            private String category;
            private List<DepartmentInfo> departments;
            private List<RankInfo> ranks;

            public static CheckResponse of(Company company, List<Department> departments, List<Rank> ranks) {
                return CheckResponse.builder()
                        .companyId(company.getCompanyId())
                        .companyCode(company.getCompanyCode())
                        .companyName(company.getCompanyName())
                        .category(company.getCategory())
                        .departments(departments.stream()
                                .map(DepartmentInfo::of)
                                .toList())
                        .ranks(ranks.stream()
                                .map(RankInfo::of)
                                .toList())
                        .build();
            }
        }
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class JoinRequest {
            @NotBlank(message = "회사 코드는 필수입니다.")
            private String companyCode;

            @NotNull(message = "부서는 필수입니다.")
            private Long departmentId;

            @NotNull(message = "직급은 필수입니다.")
            private Long rankId;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class JoinResponse {
            private Long companyId;
            private String companyCode;
            private String companyName;
            private String category;
            private String message;
            private String token;
            private CommonEnums.Status joinStatus;

            public static JoinResponse of(Company company, CommonEnums.Status joinStatus, String token) {
                String message = (joinStatus == CommonEnums.Status.N)
                        ? "관리자 승인 대기 중입니다."
                        : "회사 참여가 완료되었습니다.";
                return JoinResponse.builder()
                        .companyId(company.getCompanyId())
                        .companyCode(company.getCompanyCode())
                        .companyName(company.getCompanyName())
                        .category(company.getCategory())
                        .message(message)
                        .token(token)
                        .joinStatus(joinStatus)
                        .build();
            }
        }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JoinListItemRes {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String departmentName;
        private String rankName;
        private String joinStatus;

        public static JoinListItemRes of(Member member) {
            String dept = member.getDepartment() != null ? member.getDepartment().getDepartmentName() : null;
            String rank = member.getRank() != null ? member.getRank().getRankName() : null;
            String status = member.getStatus() == CommonEnums.Status.N ? "PENDING" : (member.getStatus() == CommonEnums.Status.Y ? "APPROVED" : "REJECTED");
            return JoinListItemRes.builder()
                    .id(member.getMemberId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .departmentName(dept)
                    .rankName(rank)
                    .joinStatus(status)
                    .build();
        }
    }
}
