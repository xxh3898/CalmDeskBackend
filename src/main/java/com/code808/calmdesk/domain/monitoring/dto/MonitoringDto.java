package com.code808.calmdesk.domain.monitoring.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringDto {

    private Stats stats;
    private List<Trend> trend;
    private List<Distribution> distribution;
    private List<DeptComparison> deptComparison;
    private List<Factor> factors;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stats {

        private String totalEmployees; // "142명"
        private String avgStress;      // "34.2%"
        private String highRiskCount;  // "12명"
        private String avgCooldown;    // "3.2회"
        private String consultationCount; // "172건"

        // Trends (e.g., "+2", "-4.1%")
        private String employeeTrend;
        private String stressTrend;
        private String riskTrend;
        private String cooldownTrend;
        private String consultationTrend;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trend {

        private String month;       // "1월"
        private double stress;      // 34
        private int consultation;   // 172
        private int cooldown;       // 48
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Distribution {

        private String name;    // "위험 (70%+)"
        private int value;      // 12 (percentage or count)
        private String color;   // "#fb7185"
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeptComparison {

        private String dept;    // "상담 1팀"
        private double avg;     // 42
        private int highRisk;   // 4
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Factor {

        private String factor;  // "업무량"
        private int value;      // 45
    }

    /**
     * 스트레스 점수 환산 로직 (1~5점 -> 0~100점 비선형 변환) 1 (최고) -> 0 2 (좋음) -> 10 3 (보통) ->
     * 30 4 (우울) -> 70 5 (힘듦) -> 100
     */
    public static int convertScore(double rawScore) {
        if (rawScore <= 1.0) {
            return 0;
        }
        if (rawScore >= 5.0) {
            return 100;
        }

        // 구간별 선형 보간 (Linear Interpolation)
        if (rawScore <= 2.0) {
            // 1~2 구간 -> 0~10
            return (int) Math.round((rawScore - 1.0) * 10);
        } else if (rawScore <= 3.0) {
            // 2~3 구간 -> 10~30 (차이 20)
            return (int) Math.round(10 + (rawScore - 2.0) * 20);
        } else if (rawScore <= 4.0) {
            // 3~4 구간 -> 30~70 (차이 40)
            return (int) Math.round(30 + (rawScore - 3.0) * 40);
        } else {
            // 4~5 구간 -> 70~100 (차이 30)
            return (int) Math.round(70 + (rawScore - 4.0) * 30);
        }
    }
}
