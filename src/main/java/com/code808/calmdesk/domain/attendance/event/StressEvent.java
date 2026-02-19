package com.code808.calmdesk.domain.attendance.event;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StressEvent {
    private final Long companyId;
    private final Long memberId;
    private final LocalDate summaryDate;
    private final Double avgStressLevel;

    public StressEvent(Long companyId, Long memberId, LocalDate summaryDate, Double avgStressLevel) {
        this.companyId = companyId;
        this.memberId = memberId;
        this.summaryDate = summaryDate;
        this.avgStressLevel = avgStressLevel;
    }
}
