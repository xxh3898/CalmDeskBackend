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
    /** 최신 스트레스 요약의 평균 스트레스 수준(0~100 등). 없으면 null */
    private Integer stress;

    public static TeamMemberResponse from(Member member, Integer remainingLeave, Integer stress) {
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
                .stress(stress)
                .build();
    }
}
