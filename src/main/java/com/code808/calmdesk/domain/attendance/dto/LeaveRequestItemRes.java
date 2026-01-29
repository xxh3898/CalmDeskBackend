package com.code808.calmdesk.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트 Attendance 페이지 - 휴가 현황용
 * { id, type, period, status, days }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestItemRes {

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("type")
    private String type;   // 연차, 반차, 워케이션
    
    @JsonProperty("period")
    private String period; // 2026.01.25 - 01.26 / 2026.01.14 (오후)
    
    @JsonProperty("status")
    private String status; // 승인대기, 승인완료
    
    @JsonProperty("days")
    private String days;   // 2일, 0.5일, 0.0일
}
