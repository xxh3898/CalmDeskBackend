package com.code808.calmdesk.domain.callrecord.dto;

import com.code808.calmdesk.domain.callrecord.entity.CallRecord;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CallRecordDto {

    /** 통화 종료 후 녹음 업로드 + 메타 요청 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UploadRequest {
        @NotBlank(message = "고객 전화번호는 필수입니다.")
        private String customerPhone;

        @NotNull(message = "통화 시작 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime callStartedAt;

        @NotNull(message = "통화 종료 시각은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime callEndedAt;
    }

    /** 목록 한 건 (내 통화 / 전체) */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListItem {
        private Long id;
        private String customerPhone;
        private LocalDateTime callStartedAt;
        private LocalDateTime callEndedAt;
        private Integer profanityCount;
        private String status;
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

    /** 전화번호 검색 결과 한 건 (날짜별 욕설) */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhoneSearchItem {
        private Long id;
        private LocalDateTime callStartedAt;
        private LocalDateTime callEndedAt;
        private Integer profanityCount;
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
