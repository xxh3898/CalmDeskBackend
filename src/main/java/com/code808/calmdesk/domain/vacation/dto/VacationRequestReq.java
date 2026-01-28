package com.code808.calmdesk.domain.vacation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 프론트 Attendance 페이지 - 휴가 신청용 Request DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequestReq {

    @JsonProperty("type")
    private String type;   // "연차", "반차", "워케이션"

    @JsonProperty("startDate")
    private LocalDate startDate;  // 시작일

    @JsonProperty("endDate")
    private LocalDate endDate;    // 종료일

    @JsonProperty("reason")
    private String reason;         // 신청 사유 (선택 사항)
}
