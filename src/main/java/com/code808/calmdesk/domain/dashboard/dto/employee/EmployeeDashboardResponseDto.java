package com.code808.calmdesk.domain.dashboard.dto.employee;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeDashboardResponseDto {

    private UserProfile userProfile;
    private StressStats stressStats;
    private AttendanceStats attendanceStats;
    private VacationStats vacationStats;
    private PointStats pointStats;
    private WeeklyStressChart weeklyStressChart;

    @Getter
    @Builder
    public static class UserProfile {

        private String name;
    }

    @Getter
    @Builder
    public static class StressStats {

        private Integer score;
        private String status; // "매우 양호", "주의", "위험"
    }

    @Getter
    @Builder
    public static class AttendanceStats {

        private Integer attendanceRate;
        private String statusMessage; // "지각/결근 없음", 지각: 1회, 결근: 1회 이런식으로 출력
        private String currentStatus; // "업무 준비 중", "업무 중", "자리비움" 등
    }

    @Getter
    @Builder
    public static class VacationStats {

        private Double remainingDays;
        private Integer totalDays;
        private Double usedDays;
    }

    @Getter
    @Builder
    public static class PointStats {

        private Integer amount;
    }

    @Getter
    @Builder
    public static class WeeklyStressChart {

        private List<DailyStress> thisWeek;
        private List<DailyStress> lastWeek;

        @Getter
        @Builder
        public static class DailyStress {

            private String day;
            private Integer value;
        }
    }
}
