package com.code808.calmdesk.domain.callrecord.dto;

import com.code808.calmdesk.domain.callrecord.entity.CallRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CallRecordDto {

    /**
     * 통화 종료 후 녹음 업로드 + 메타 요청
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UploadRequest {

        @Schema(description = "고객 전화번호", example = "010-1234-5678")
        @NotBlank(message = "고객 전화번호는 필수입니다.")
        private String customerPhone;

        @Schema(description = "통화 시작 시각", example = "2026-02-25T14:00:00")
        @NotNull(message = "통화 시작 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime callStartedAt;

        @Schema(description = "통화 종료 시각", example = "2026-02-25T14:10:00")
        @NotNull(message = "통화 종료 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime callEndedAt;
    }

    /**
     * 목록 한 건 (내 통화 / 전체)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListItem {

        @Schema(description = "기록 ID", example = "101")
        private Long id;
        @Schema(description = "고객 전화번호", example = "010-1234-5678")
        private String customerPhone;
        @Schema(description = "통화 시작 시각", example = "2026-02-25T14:00:00")
        private LocalDateTime callStartedAt;
        @Schema(description = "통화 종료 시각", example = "2026-02-25T14:10:00")
        private LocalDateTime callEndedAt;
        @Schema(description = "욕설 횟수", example = "2")
        private Integer profanityCount;
        @Schema(description = "처리 상태 (PENDING, COMPLETED, FAILED)", example = "COMPLETED")
        private String status;
        @Schema(description = "담당 직원 이름", example = "홍길동")
        private String employeeName;  // 전체일 때만 의미 있음

        public static ListItem from(CallRecord c) {
            return ListItem.builder()
                    .id(c.getId())
                    .customerPhone(c.getCustomerPhone())
                    .callStartedAt(c.getCallStartedAt())
                    .callEndedAt(c.getCallEndedAt())
                    .profanityCount(c.getProfanityCount())
                    .status(c.getStatus().name())
                    .employeeName(c.getEmployee() != null ? c.getEmployee().getName() : null)
                    .build();
        }
    }

    /**
     * 전화번호 검색 결과 한 건 (날짜별 욕설)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhoneSearchItem {

        @Schema(description = "기록 ID", example = "101")
        private Long id;
        @Schema(description = "통화 시작 시각", example = "2026-02-25T14:00:00")
        private LocalDateTime callStartedAt;
        @Schema(description = "통화 종료 시각", example = "2026-02-25T14:10:00")
        private LocalDateTime callEndedAt;
        @Schema(description = "욕설 횟수", example = "2")
        private Integer profanityCount;
        @Schema(description = "담당 직원 이름", example = "홍길동")
        private String employeeName;

        public static List<PhoneSearchItem> fromList(List<CallRecord> list) {
            return list.stream()
                    .map(c -> PhoneSearchItem.builder()
                    .id(c.getId())
                    .callStartedAt(c.getCallStartedAt())
                    .callEndedAt(c.getCallEndedAt())
                    .profanityCount(c.getProfanityCount())
                    .employeeName(c.getEmployee() != null ? c.getEmployee().getName() : null)
                    .build())
                    .collect(Collectors.toList());
        }
    }
}
