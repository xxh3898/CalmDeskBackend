package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Rank;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

public class CompanyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CodeResponse {

        @Schema(description = "생성된 회사 코드", example = "ABC12345")
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

        @Schema(description = "회사 코드 (선택)", example = "ABC12345")
        private String companyCode;

        @Schema(description = "회사명", example = "코드808")
        @NotBlank(message = "회사명은 필수입니다")
        @Size(min = 2, max = 50, message = "회사명은 2자 이상 50자 이하여야 합니다")
        private String companyName;

        @Schema(description = "업종", example = "IT 서비스")
        @NotBlank(message = "업종은 필수입니다")
        @Size(max = 50, message = "업종은 50자 이하여야 합니다")
        private String category;

        @Schema(description = "최소 매출액 (예)", example = "1000")
        @NotNull(message = "최소 금액은 필수입니다")
        @Min(value = 0, message = "최소 금액은 0 이상이어야 합니다")
        private Integer minValue;

        @Schema(description = "최대 매출액 (예)", example = "5000")
        @NotNull(message = "최대 금액은 필수입니다")
        @Min(value = 0, message = "최대 금액은 0 이상이어야 합니다")
        private Integer maxValue;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterResponse {

        @Schema(description = "회사 ID", example = "1")
        private Long companyId;
        @Schema(description = "회사 코드", example = "ABC12345")
        private String companyCode;
        @Schema(description = "회사명", example = "코드808")
        private String companyName;
        @Schema(description = "업종", example = "IT 서비스")
        private String category;
        @Schema(description = "최소 매출액", example = "1000")
        private Integer minValue;
        @Schema(description = "최대 매출액", example = "5000")
        private Integer maxValue;
        @Schema(description = "인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
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

        @Schema(description = "부서 ID", example = "2")
        private Long departmentId;
        @Schema(description = "부서 명칭", example = "개발팀")
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

        @Schema(description = "직급 ID", example = "1")
        private Long rankId;
        @Schema(description = "직급 명칭", example = "대리")
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

        @Schema(description = "회사 ID", example = "1")
        private Long companyId;
        @Schema(description = "회사 코드", example = "ABC12345")
        private String companyCode;
        @Schema(description = "회사 이름", example = "코드808")
        private String companyName;
        @Schema(description = "카테고리 (업종)", example = "IT")
        private String category;
        @Schema(description = "부서 목록")
        private List<DepartmentInfo> departments;
        @Schema(description = "직급 목록")
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

        @Schema(description = "참여할 회사 코드", example = "ABC12345")
        @NotBlank(message = "회사 코드는 필수입니다.")
        private String companyCode;

        @Schema(description = "선택한 부서 ID", example = "2")
        @NotNull(message = "부서는 필수입니다.")
        private Long departmentId;

        @Schema(description = "선택한 직급 ID", example = "3")
        @NotNull(message = "직급은 필수입니다.")
        private Long rankId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JoinResponse {

        @Schema(description = "회사 ID", example = "1")
        private Long companyId;
        @Schema(description = "회사 코드", example = "ABC12345")
        private String companyCode;
        @Schema(description = "회사명", example = "코드808")
        private String companyName;
        @Schema(description = "업종", example = "IT 서비스")
        private String category;
        @Schema(description = "결과 메시지", example = "관리자 승인 대기 중입니다.")
        private String message;
        @Schema(description = "인증 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String token;
        @Schema(description = "참여 상태 (N: 대기, Y: 승인, R: 반려)", example = "N")
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

        @Schema(description = "사용자 ID", example = "5")
        private Long id;
        @Schema(description = "사용자 이름", example = "김철수")
        private String name;
        @Schema(description = "이메일", example = "chulsoo@example.com")
        private String email;
        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;
        @Schema(description = "부서명", example = "인사팀")
        private String departmentName;
        @Schema(description = "직급명", example = "사원")
        private String rankName;
        @Schema(description = "입사 신청 상태 (PENDING, APPROVED, REJECTED)", example = "PENDING")
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
