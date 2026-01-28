package com.code808.calmdesk.domain.vacation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트 Attendance 페이지 - 휴가 신청 응답용 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequestRes {

    @JsonProperty("id")
    private Long id;  // 생성된 휴가 ID

    @JsonProperty("message")
    private String message;  // 성공 메시지
}
