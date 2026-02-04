package com.code808.calmdesk.domain.team.dto;

import com.code808.calmdesk.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponse {

    private Long memberId;
    private String name;
    private String email;
    private String phone;
    private String departmentName;
    private String rankName;
    private String joinDate;
    private Integer remainingLeave;

    public static TeamMemberResponse from(Member member, Integer remainingLeave) {
        String departmentName = member.getDepartment() != null && member.getDepartment().getDepartmentName() != null
                ? member.getDepartment().getDepartmentName() : "";
        String rankName = member.getRank() != null && member.getRank().getRankName() != null
                ? member.getRank().getRankName() : "";
        String joinDateStr = member.getJoinDate() != null
                ? member.getJoinDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                : "";
        return TeamMemberResponse.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .email(member.getEmail() != null ? member.getEmail() : "")
                .phone(member.getPhone() != null ? member.getPhone() : "")
                .departmentName(departmentName)
                .rankName(rankName)
                .joinDate(joinDateStr)
                .remainingLeave(remainingLeave != null ? remainingLeave : 0)
                .build();
    }
}
