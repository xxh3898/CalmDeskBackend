package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DepartmentDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailResponse {

        @Schema(description = "부서 ID", example = "2")
        private Long departmentId;
        @Schema(description = "부서명", example = "인사팀")
        private String departmentName;
        @Schema(description = "회사 ID", example = "1")
        private Long companyId;
        @Schema(description = "소속 부서원 수", example = "15")
        private int memberCount;

        public static DetailResponse from(Department department) {
            return DetailResponse.builder()
                    .departmentId(department.getDepartmentId())
                    .departmentName(department.getDepartmentName())
                    .companyId(department.getCompany().getCompanyId())
                    .memberCount(department.getMembers().size())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberResponse {

        @Schema(description = "사용자 ID", example = "5")
        private Long memberId;
        @Schema(description = "이름", example = "김철수")
        private String name;
        @Schema(description = "역할/직급", example = "과장")
        private String role;
        @Schema(description = "현재 상태", example = "업무 중")
        private String status;
        @Schema(description = "이메일", example = "chulsoo@example.com")
        private String email;
        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;
        @Schema(description = "아바타 이미지 URL (현재 미구현)", example = "null")
        private String avatar;

        public static MemberResponse from(Member member) {
            return from(member, "출근 전");
        }

        public static MemberResponse from(Member member, String status) {
            return MemberResponse.builder()
                    .memberId(member.getMemberId())
                    .name(member.getName())
                    .role(member.getRank() != null ? member.getRank().getRankName() : "사원")
                    .status(status)
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .avatar(null) // TODO: 프로필 이미지 구현 시 추가
                    .build();
        }
    }
}
