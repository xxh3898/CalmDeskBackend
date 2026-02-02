package com.code808.calmdesk.domain.vacation.dto;

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
        private String type;         // "연차", "반차", "워케이션"
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
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
        private Long id;
        private String message;

        public static VacationRequestRes of(Long id, String message) {
            return VacationRequestRes.builder()
                    .id(id)
                    .message(message)
                    .build();
        }
    }
}
