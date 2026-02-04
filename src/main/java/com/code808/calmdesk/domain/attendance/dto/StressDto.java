package com.code808.calmdesk.domain.attendance.dto;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class StressDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryRequest {
        private Long memberId;
        private LocalDate summaryDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryResponse {
        private Double avgStressLevel;
        private Integer normalizedScore;
        private Integer checkinCount;
        private Long memberId;
        private Long departmentId;
        private LocalDate summaryDate;

        public static SummaryResponse of(StressSummary summary){
            return SummaryResponse.builder()
                    .avgStressLevel(summary.getAvgStressLevel())
                    .normalizedScore((int) Math.round(summary.getAvgStressLevel() * 20))
                    .checkinCount(summary.getCheckinCount())
                    .memberId(summary.getMember() != null ? summary.getMember().getMemberId() : null)
                    .summaryDate(summary.getSummaryDate())
                    .build();
        }
    }
}