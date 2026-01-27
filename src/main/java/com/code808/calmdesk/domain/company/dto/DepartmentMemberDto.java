package com.code808.calmdesk.domain.company.dto;

import com.code808.calmdesk.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentMemberDto {

    private Long memberId;
    private String name;
    private String role;
    private String status;
    private String email;
    private String phone;
    private String avatar;

    public static DepartmentMemberDto from(Member member) {
        return DepartmentMemberDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                // 직급이 null일 경우 '사원' 기본값 처리
                .role(member.getRank() != null ? member.getRank().getRankName() : "사원")
                // TODO: 추후 Attendance 도메인 연동 시 실제 상태로 변경 (현재는 하드코딩)
                .status("업무 중")
                .email(member.getEmail())
                .phone(member.getPhone())
                // TODO: 프로필 이미지 구현 시 추가
                .avatar(null)
                .build();
    }
}
