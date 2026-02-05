package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StressResponse {

    /** 해당 period 기준 평균 스트레스 (0~100). 마이페이지는 주간(이번 주) 기준 */
    private Double avgStress;
    private String level;            // LOW, NORMAL, HIGH, CRITICAL
    private String levelText;        // 한글 레벨명
    private String message;          // 상태 메시지
    private String description;      // 상세 설명
    /** 기간 표시. 예: "이번 주 (2026.02.03 ~ 2026.02.09)" → avgStress는 이 구간 평균 */
    private String period;

    /**
     * StressSummary 엔티티 기준 응답 생성 (avgStressLevel, summaryDate, checkinCount 반영)
     */
    public static StressResponse from(StressSummary summary) {
        if (summary == null) {
            return createDefault();
        }
        // 원시 점수(1~5)를 0~100 점수로 환산
        double raw = summary.getAvgStressLevel() != null ? summary.getAvgStressLevel() : 0.0;
        int converted = MonitoringDto.convertScore(raw);
        double avg = converted;
        String level;
        String levelText;
        String message;
        String description;
        if (avg <= 25) {
            level = "LOW";
            levelText = "안정";
            message = "스트레스 수준이 낮습니다.";
            description = "현재 상태가 양호합니다.";
        } else if (avg <= 50) {
            level = "NORMAL";
            levelText = "보통";
            message = "스트레스 수준이 보통입니다.";
            description = "적당한 휴식을 권장합니다.";
        } else if (avg <= 75) {
            level = "HIGH";
            levelText = "주의";
            message = "스트레스 수준이 높습니다.";
            description = "휴식과 스트레스 관리가 필요합니다.";
        } else {
            level = "CRITICAL";
            levelText = "위험";
            message = "스트레스 수준이 매우 높습니다.";
            description = "충분한 휴식과 관리자 상담을 권장합니다.";
        }
        String period = summary.getSummaryDate() != null
                ? summary.getSummaryDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                : "이번 주";
        int checkin = summary.getCheckinCount() != null ? summary.getCheckinCount() : 0;
        if (checkin > 0) {
            description = description + " (기준일 출석 " + checkin + "회)";
        }
        return StressResponse.builder()
                .avgStress(avg)
                .level(level)
                .levelText(levelText)
                .message(message)
                .description(description)
                .period(period)
                .build();
    }

    /**
     * 주간 스트레스 집계 기준 응답 생성 (이번 주 평균)
     *
     * @param avgStressLevelRaw 주간 평균 원시 스트레스(1~5)
     * @param periodStr         기간 표시 문자열 (예: "이번 주 (2026.02.03 ~ 2026.02.09)")
     * @param checkinCount      주간 총 체크인 횟수
     */
    public static StressResponse fromWeekly(double avgStressLevelRaw, String periodStr, int checkinCount) {
        double raw = avgStressLevelRaw;
        int converted = MonitoringDto.convertScore(raw);
        double avg = converted;
        String level;
        String levelText;
        String message;
        String description;
        if (avg <= 25) {
            level = "LOW";
            levelText = "안정";
            message = "스트레스 수준이 낮습니다.";
            description = "현재 상태가 양호합니다.";
        } else if (avg <= 50) {
            level = "NORMAL";
            levelText = "보통";
            message = "스트레스 수준이 보통입니다.";
            description = "적당한 휴식을 권장합니다.";
        } else if (avg <= 75) {
            level = "HIGH";
            levelText = "주의";
            message = "스트레스 수준이 높습니다.";
            description = "휴식과 스트레스 관리가 필요합니다.";
        } else {
            level = "CRITICAL";
            levelText = "위험";
            message = "스트레스 수준이 매우 높습니다.";
            description = "충분한 휴식과 관리자 상담을 권장합니다.";
        }
        if (checkinCount > 0) {
            description = description + " (이번 주 출석 " + checkinCount + "회)";
        }
        return StressResponse.builder()
                .avgStress(avg)
                .level(level)
                .levelText(levelText)
                .message(message)
                .description(description)
                .period(periodStr != null ? periodStr : "이번 주")
                .build();
    }

    /**
     * 데이터 없을 때 기본값
     */
    public static StressResponse createDefault() {
        return StressResponse.builder()
                .avgStress(0.0)
                .level("LOW")
                .levelText("안정")
                .message("데이터 수집 중")
                .description("아직 충분한 데이터가 수집되지 않았습니다. 근무 시작 후 스트레스 분석이 진행됩니다.")
                .period("이번 주")
                .build();
    }
}
