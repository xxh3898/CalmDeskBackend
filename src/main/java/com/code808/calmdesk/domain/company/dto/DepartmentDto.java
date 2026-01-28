package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.member.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;

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

        private Long departmentId;
        private String departmentName;
        private Long companyId;
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

        private Long memberId;
        private String name;
        private String role;
        private String status;
        private String email;
        private String phone;
        private String avatar;

        public static MemberResponse from(Member member) {
            return MemberResponse.builder()
                    .memberId(member.getMemberId())
                    .name(member.getName())
                    .role(member.getRank() != null ? member.getRank().getRankName() : "사원")
                    .status("업무 중") // TODO: 추후 Attendance 도메인 연동 시 수정
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .avatar(null) // TODO: 프로필 이미지 구현 시 추가
                    .build();
        }
    }
}
