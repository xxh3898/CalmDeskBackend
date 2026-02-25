package com.code808.calmdesk.domain.Notification.dto;

import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "알림 ID", example = "10")
    private Long id;
    @Schema(description = "알림 제목", example = "신규 메시지")
    private String title;
    @Schema(description = "알림 내용", example = "홍길동님으로부터 새로운 메시지가 도착했습니다.")
    private String content;
    @Schema(description = "리다이렉트 URL", example = "/chat/room/123")
    private String redirectUrl;
    @Schema(description = "읽음 상태 (READ, UNREAD)", example = "UNREAD")
    private String status;
    @Schema(description = "대상 권한 (MEMBER, ADMIN 등)", example = "MEMBER")
    private String targetRole;
    @Schema(description = "생성 일시", example = "2026-02-25T15:20:00")
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
