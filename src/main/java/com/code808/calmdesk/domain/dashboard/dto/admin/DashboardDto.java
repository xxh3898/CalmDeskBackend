package com.code808.calmdesk.domain.dashboard.dto.admin;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.DepartmentStatsProjection;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class DashboardDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentStats {
        private Long departmentId;
        private String departmentName;
        private Double avgStressLevel;
        private Long memberCount;

        public static DepartmentStats of(DepartmentStatsProjection projection) {
            return DepartmentStats.builder()
                    .departmentId(projection.getDepartmentId())
                    .departmentName(projection.getDepartmentName())
                    .avgStressLevel(projection.getAvgStressLevel())
                    .memberCount(projection.getMemberCount())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyStats {
        private Double avgStressLevel;
        private Double avgStreessPercentage;
        private Double stressChange;
        private Long totalMembers;
        private Long highRiskCount;

        public static CompanyStats of(Double todayAvg, Double yesterdayAvg,
                                      Long totalMembers, Long highRiskCount) {
            Double maxScore = 5.0;
            Double todayAvgPct = (todayAvg != null) ? (todayAvg/maxScore) * 100 : 0.0;
            Double yesterdayAvgPct = (yesterdayAvg != null) ? (yesterdayAvg/maxScore) * 100 : 0.0;

            return CompanyStats.builder()
                    .avgStressLevel(todayAvg)
                    .avgStreessPercentage(todayAvgPct)
                    .stressChange(todayAvgPct != null && yesterdayAvgPct != null ? todayAvgPct - yesterdayAvgPct : null)
                    .totalMembers(totalMembers)
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
        private LocalDate summaryDate;

        public static HighRiskMember of(StressSummary summary) {
            return HighRiskMember.builder()
                    .memberId(summary.getMember().getMemberId())
                    .memberName(summary.getMember().getName())
                    .departmentName(summary.getDepartment().getDepartmentName())
                    .stressLevel(summary.getAvgStressLevel())
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
    public static class DashboardRequest {

        @NotNull(message = "회사 ID는 필수 항목입니다.")
        private Long companyId;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;

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
}