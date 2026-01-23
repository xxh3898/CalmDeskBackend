package com.code808.calmdesk.domain.member.employee.myPage.dto;

import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long memberId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String position;
    private String joinDate;
    private Integer currentPoint;

    public static ProfileResponse from(Member member) {
        int currentPoint = member.getTotalEarnedPoint() - member.getTotalSpentPoint();
        
        return ProfileResponse.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .department(member.getDepartment().getDepartmentName())
                .position(member.getRank().getRankName())
                .joinDate(member.getCreatedDate() != null 
                        ? member.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                        : "")
                .currentPoint(currentPoint)
                .build();
    }
}
