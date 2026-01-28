package com.code808.calmdesk.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트 Attendance 페이지 - 요약 카드용
 * 이번 달 출근 14/21일, 지각/결근 1건, 잔여 연차 12.5일, 이번 주 근무 28.5시간
 * 프론트: val + total 조합 (예: val "14", total "/ 21일")
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryRes {

    @JsonProperty("monthWorkDays")
    private int monthWorkDays;      // 14
    
    @JsonProperty("monthTotalDays")
    private int monthTotalDays;     // 21
    
    @JsonProperty("lateOrAbsenceCount")
    private int lateOrAbsenceCount; // 1
    
    @JsonProperty("remainingVacation")
    private double remainingVacation; // 12.5
    
    @JsonProperty("weekWorkHours")
    private double weekWorkHours;   // 28.5
}
