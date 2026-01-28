package com.code808.calmdesk.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트 Attendance 페이지 - 전체 기록 타임라인 / 일별 상세용
 * { id, day, date, clockIn, clockOut, status, duration, note }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceHistoryItemRes {

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("day")
    private int day;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("clockIn")
    private String clockIn;
    
    @JsonProperty("clockOut")
    private String clockOut;
    
    @JsonProperty("status")
    private String status;  // 정상, 지각
    
    @JsonProperty("duration")
    private String duration; // 9h 13m
    
    @JsonProperty("note")
    private String note;
}
