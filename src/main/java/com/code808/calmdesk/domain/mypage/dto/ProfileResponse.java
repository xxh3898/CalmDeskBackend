package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "사용자 ID", example = "1")
    private Long memberId;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "이메일", example = "hong@example.com")
    private String email;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @Schema(description = "소속 회사명", example = "코드808")
    private String companyName;
    @Schema(description = "부서명", example = "개발팀")
    private String department;
    @Schema(description = "직급", example = "사원")
    private String position;
    @Schema(description = "가입일 (yyyy.MM.dd)", example = "2024.01.01")
    private String joinDate;
    @Schema(description = "현재 보유 포인트", example = "5000")
    private Integer currentPoint;

    /**
     * @param currentPoint gifticon 도메인 Point_History 기준 계산된 현재 포인트 (가장 최근
     * balanceAfter 또는 0)
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
                        : "-")
                .currentPoint(currentPoint)
                .build();
    }
}
