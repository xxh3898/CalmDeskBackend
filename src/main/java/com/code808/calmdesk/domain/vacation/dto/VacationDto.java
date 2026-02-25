package com.code808.calmdesk.domain.vacation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 휴가(Vacation) 관련 DTO - CompanyDto와 동일한 형태로 static 내부 클래스 구성
 */
public class VacationDto {

    /**
     * 프론트 Attendance 페이지 - 휴가 신청용 Request DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VacationRequestReq {

        @Schema(description = "휴가 종류 (연차, 반차, 워케이션)", example = "연차")
        private String type;         // "연차", "반차", "워케이션"
        @Schema(description = "시작 날짜", example = "2026-03-01")
        private LocalDate startDate;
        @Schema(description = "종료 날짜", example = "2026-03-05")
        private LocalDate endDate;
        @Schema(description = "신청 사유", example = "가족 여행")
        private String reason;
        @Schema(description = "반차 종류 (오전, 오후)", example = "오전")
        private String halfDayType;  // 반차일 경우 "오전" 또는 "오후"
    }

    /**
     * 프론트 Attendance 페이지 - 휴가 신청 응답용 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VacationRequestRes {

        @Schema(description = "휴가 ID", example = "50")
        private Long id;
        @Schema(description = "처리 결과 메시지", example = "휴가 신청이 완료되었습니다.")
        private String message;

        public static VacationRequestRes of(Long id, String message) {
            return VacationRequestRes.builder()
                    .id(id)
                    .message(message)
                    .build();
        }
    }
}
