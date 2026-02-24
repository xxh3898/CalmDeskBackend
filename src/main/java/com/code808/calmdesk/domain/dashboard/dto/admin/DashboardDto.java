package com.code808.calmdesk.domain.dashboard.dto.admin;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.DepartmentStatsProjection;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;


public class DashboardDto {

    public static int convertScore(double rawScore) {
        if (rawScore <= 1.0) {
            return 0;
        }
        if (rawScore >= 5.0) {
            return 100;
        }
        // 구간별 선형 보간 (Linear Interpolation)
        if (rawScore <= 2.0) {
            return (int) Math.round((rawScore - 1.0) * 10);
        } else if (rawScore <= 3.0) {
            return (int) Math.round(10 + (rawScore - 2.0) * 20);
        } else if (rawScore <= 4.0) {
            return (int) Math.round(30 + (rawScore - 3.0) * 40);
        } else {
            return (int) Math.round(70 + (rawScore - 4.0) * 30);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentStats {
        private Long departmentId;
        private String departmentName;
        private Double avgStressLevel;
        private Double avgStressPercentage;
        private Long memberCount;
        private Long cooldownCount;

        public static DepartmentStats of(DepartmentStatsProjection projection) {
            Double avgStress = projection.getAvgStressLevel();
            Double avgStressPct = (avgStress != null) ? (double) convertScore(avgStress) : 0.0;

            return DepartmentStats.builder()
                    .departmentId(projection.getDepartmentId())
                    .departmentName(projection.getDepartmentName())
                    .avgStressLevel(avgStress)
                    .avgStressPercentage(avgStressPct)
                    .memberCount(projection.getMemberCount())
                    .cooldownCount(projection.getCooldownCount())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyStats {
        private Double avgStressLevel;
        private Double avgStressPercentage;
        private Long totalMembers;
        private Double todayAttendance;
        private Long consultationCount;
        private Long vacationCount;
        private Long highRiskCount;

        public static CompanyStats of(Double todayAvg, Long totalMembers, Long highRiskCount,
                                      Double todayAttendance, Long consultationCount, Long vacationCount) {

            Double todayAvgPct = (todayAvg != null)
                    ? Math.round(convertScore(todayAvg) * 10.0) / 10.0 : 0.0;

            Double todayAtnd = (todayAttendance != null)
                    ? Math.round(todayAttendance * 10.0) / 10.0 : 0.0;

            return CompanyStats.builder()
                    .avgStressLevel(todayAvg)
                    .avgStressPercentage(todayAvgPct)
                    .totalMembers(totalMembers)
                    .todayAttendance(todayAtnd)
                    .consultationCount(consultationCount)
                    .vacationCount(vacationCount)
                    .highRiskCount(highRiskCount)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HighRiskMember {
        private Long memberId;
        private String memberName;
        private String departmentName;
        private Double stressLevel;
        private Double stressPercentage;
        private LocalDate summaryDate;

        public static HighRiskMember of(StressSummary summary) {
            Double stress = summary.getAvgStressLevel();
            Double stressPct = (stress != null) ? (double) convertScore(stress) : 0.0;

            return HighRiskMember.builder()
                    .memberId(summary.getMember().getMemberId())
                    .memberName(summary.getMember().getName())
                    .departmentName(summary.getDepartment().getDepartmentName())
                    .stressLevel(stress)
                    .stressPercentage(stressPct)
                    .summaryDate(summary.getSummaryDate())
                    .build();
        }

        public static List<HighRiskMember> ofList(List<StressSummary> summaries) {
            return summaries.stream()
                    .map(HighRiskMember::of)
                    .toList();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardRequest {

        private Long companyId;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;

        @Builder.Default
        @Min(value = 0, message = "임계값은 0 이상이어야 합니다.")
        private Integer threshold = 4;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DashboardResponse {
        private LocalDate date;
        private CompanyStats companyStats;
        private List<DepartmentStats> departmentStats;
        private List<HighRiskMember> highRiskMembers;
        public static DashboardResponse of(LocalDate date,
                                           CompanyStats companyStats,
                                           List<DepartmentStats> departmentStats,
                                           List<HighRiskMember> highRiskMembers) {
            return DashboardResponse.builder()
                    .date(date)
                    .companyStats(companyStats)
                    .departmentStats(departmentStats)
                    .highRiskMembers(highRiskMembers)
                    .build();
        }
    }

//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class YesterdayStats {
//        private LocalDate date;
//        private Double avgStressPercentage;
//        private Double attendanceRate;
//
//        public static YesterdayStats of(LocalDate date, Double yesterdayAvg, Double yesterdayAttendance) {
//            Double avgStressPct = (yesterdayAvg != null)
//                    ? Math.round(convertScore(yesterdayAvg) * 10.0) / 10.0 : 0.0;
//            Double yesterdayAtnd = (yesterdayAttendance != null)
//                    ? Math.round(yesterdayAttendance * 10.0) / 10.0 : 0.0;
//
//            return YesterdayStats.builder()
//                    .date(date)
//                    .avgStressPercentage(avgStressPct)
//                    .attendanceRate(yesterdayAtnd)
//                    .build();
//        }
//    }
}