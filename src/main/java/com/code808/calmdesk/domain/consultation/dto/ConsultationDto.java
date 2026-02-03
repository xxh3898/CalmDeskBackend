package com.code808.calmdesk.domain.consultation.dto;

import java.time.LocalDateTime;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.fasterxml.jackson.annotation.JsonProperty;

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

        @JsonProperty("title")
        private String title;
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

        @JsonProperty("id")
        private Long id;
        @JsonProperty("title")
        private String title;
        @JsonProperty("description")
        private String description;
        @JsonProperty("status")
        private String status;
        @JsonProperty("memberName")
        private String memberName;
        @JsonProperty("departmentName")
        private String departmentName;
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
