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
}
