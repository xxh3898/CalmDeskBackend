package com.code808.calmdesk.domain.consultation.dto;

import java.time.LocalDateTime;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ConsultationDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultationCreateRequest {

        @Schema(description = "상담 제목", example = "연차 사용 문의")
        @JsonProperty("title")
        private String title;
        @Schema(description = "상담 내용 상세", example = "연차 잔여 일수 확인 부탁드립니다.")
        @JsonProperty("description")
        private String description;

        public Consultation toEntity(com.code808.calmdesk.domain.member.entity.Member member) {
            return Consultation.builder()
                    .title(this.title)
                    .description(this.description)
                    .member(member)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultationListItemRes {

        @Schema(description = "상담 ID", example = "7")
        @JsonProperty("id")
        private Long id;
        @Schema(description = "상담 제목", example = "연차 사용 문의")
        @JsonProperty("title")
        private String title;
        @Schema(description = "상담 내용 상세", example = "연차 잔여 일수 확인 부탁드립니다.")
        @JsonProperty("description")
        private String description;
        @Schema(description = "상담 상태 (WAITING, COMPLETED)", example = "WAITING")
        @JsonProperty("status")
        private String status;
        @Schema(description = "신청자 이름", example = "김철수")
        @JsonProperty("memberName")
        private String memberName;
        @Schema(description = "신청자 부서명", example = "인사팀")
        @JsonProperty("departmentName")
        private String departmentName;
        @Schema(description = "신청 일시", example = "2026-02-24T14:30:00")
        @JsonProperty("createdDate")
        private LocalDateTime createdDate;

        public static ConsultationListItemRes from(Consultation c) {
            String memberName = c.getMember() != null ? c.getMember().getName() : null;
            String deptName = c.getMember() != null && c.getMember().getDepartment() != null
                    ? c.getMember().getDepartment().getDepartmentName() : null;
            String statusStr = c.getStatus() == null ? null : c.getStatus().name();
            return ConsultationListItemRes.builder()
                    .id(c.getCounselionId())
                    .title(c.getTitle())
                    .description(c.getDescription())
                    .status(statusStr)
                    .memberName(memberName)
                    .departmentName(deptName)
                    .createdDate(c.getCreatedDate())
                    .build();
        }
    }
}
