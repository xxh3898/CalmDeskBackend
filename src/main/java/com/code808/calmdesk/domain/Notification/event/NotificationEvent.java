package com.code808.calmdesk.domain.Notification.event;

// DTO처럼 사용되는 Event 객체
public record NotificationEvent(Long memberId, String title, String content) {}