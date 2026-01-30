package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StressResponse {
    private Integer avgStress;       // 평균 스트레스 (0~100)
    private String level;            // LOW, NORMAL, HIGH, CRITICAL
    private String levelText;        // 한글 레벨명
    private String message;          // 상태 메시지
    private String description;      // 상세 설명
    private String period;           // 기간 (WEEKLY 등)

    /**
     * 스트레스 레벨별 응답 생성
     * 0~20: LOW (안정)
     * 21~50: NORMAL (보통)
     * 51~80: HIGH (주의)
     * 81~100: CRITICAL (위험)
     */
    public static StressResponse from(StressSummary summary) {
        if (summary == null) {
            return createDefault();
        }

        int stress = summary.getAvgStress() != null ? summary.getAvgStress() : 0;
        String level;
        String levelText;
        String message;
        String description;

        if (stress <= 20) {
            level = "LOW";
            levelText = "안정";
            message = "안정적인 컨디션";
            description = "현재 전반적으로 안정적인 컨디션을 유지하고 있습니다. 규칙적인 휴식과 긍정적인 마인드로 활기찬 하루를 보내세요!";
        } else if (stress <= 50) {
            level = "NORMAL";
            levelText = "보통";
            message = "양호한 컨디션";
            description = "스트레스가 다소 있지만 관리 가능한 수준입니다. 가벼운 스트레칭이나 짧은 휴식을 권장합니다.";
        } else if (stress <= 80) {
            level = "HIGH";
            levelText = "주의";
            message = "주의가 필요한 상태";
            description = "스트레스 수치가 높습니다. 충분한 휴식을 취하고, 필요시 상담을 신청해 주세요.";
        } else {
            level = "CRITICAL";
            levelText = "위험";
            message = "즉각적인 관리 필요";
            description = "스트레스가 매우 높은 상태입니다. 즉시 휴식을 취하고, 전문 상담사와 상담을 권장합니다.";
        }

        return StressResponse.builder()
                .avgStress(stress)
                .level(level)
                .levelText(levelText)
                .message(message)
                .description(description)
                .period(summary.getPeriod())
                .build();
    }

    /**
     * 데이터 없을 때 기본값
     */
    public static StressResponse createDefault() {
        return StressResponse.builder()
                .avgStress(0)
                .level("LOW")
                .levelText("안정")
                .message("데이터 수집 중")
                .description("아직 충분한 데이터가 수집되지 않았습니다. 근무 시작 후 스트레스 분석이 진행됩니다.")
                .period("WEEKLY")
                .build();
    }
}
