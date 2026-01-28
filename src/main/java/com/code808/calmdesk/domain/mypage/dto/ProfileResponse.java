package com.code808.calmdesk.domain.mypage.dto;

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
public class ProfileResponse {
    private Long memberId;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String department;
    private String position;
    private String joinDate;
    private Integer currentPoint;

    /**
     * @param currentPoint gifticon 도메인 Point_History 기준 계산된 현재 포인트 (가장 최근 balanceAfter 또는 0)
     */
    public static ProfileResponse from(Member member, int currentPoint) {
        String companyName = member.getCompany() != null && member.getCompany().getCompanyName() != null
                ? member.getCompany().getCompanyName() : "";
        String department = member.getDepartment() != null && member.getDepartment().getDepartmentName() != null
                ? member.getDepartment().getDepartmentName() : "";
        String position = member.getRank() != null && member.getRank().getRankName() != null
                ? member.getRank().getRankName() : "";

        return ProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .companyName(companyName)
                .department(department)
                .position(position)
                .joinDate(member.getRegisterDate() != null
                        ? member.getRegisterDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                        : "")
                .currentPoint(currentPoint)
                .build();
    }
}
