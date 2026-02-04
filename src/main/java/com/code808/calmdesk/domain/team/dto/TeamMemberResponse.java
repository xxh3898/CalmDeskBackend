package com.code808.calmdesk.domain.team.dto;

import com.code808.calmdesk.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponse {

    private Long memberId;
    private String name;
    private String email;
    private String departmentName;
    private String rankName;

    public static TeamMemberResponse from(Member member) {
        String departmentName = member.getDepartment() != null && member.getDepartment().getDepartmentName() != null
                ? member.getDepartment().getDepartmentName() : "";
        String rankName = member.getRank() != null && member.getRank().getRankName() != null
                ? member.getRank().getRankName() : "";
        return TeamMemberResponse.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .email(member.getEmail())
                .departmentName(departmentName)
                .rankName(rankName)
                .build();
    }
}
