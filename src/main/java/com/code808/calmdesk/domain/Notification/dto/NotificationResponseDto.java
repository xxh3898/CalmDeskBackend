package com.code808.calmdesk.domain.Notification.dto;

import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor  // 1. 기본 생성자 추가
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String content;
    private String redirectUrl;
    private String status;
    private String targetRole;
    private LocalDateTime createDate;

    // 엔티티를 DTO로 변환하는 생성자
    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.redirectUrl = notification.getRedirectUrl();
        this.status = notification.getStatus();
        this.targetRole = notification.getTargetRole();
        this.createDate = notification.getCreateDate();
    }
}