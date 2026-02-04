package com.code808.calmdesk.domain.Notification.controller;

import com.code808.calmdesk.domain.Notification.dto.NotificationResponseDto;
import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.service.NotificationService;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor // 리액트 포트 허용
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    // 1. SSE 구독 (기존 로직 유지 + 서비스 위임)
    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long memberId) {
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);
        notificationService.addEmitter(memberId, emitter);

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            // 예외 처리
        }
        return emitter;
    }

    // 2. 초기 알림 내역 조회 (프론트 fetchNotifications 대응)
    @GetMapping("/api/notifications/{memberId}")
    public List<NotificationResponseDto> getNotifications(@PathVariable Long memberId) {
        // 1. 리포지토리에서 엔티티 리스트를 가져옵니다.
        List<Notification> notifications = notificationRepository.findAllByMemberIdOrderByCreateDateDesc(memberId);

        // 2. 엔티티 리스트를 DTO 리스트로 변환해서 반환합니다.
        return notifications.stream()
                .map(NotificationResponseDto::new) // 생성자 참조를 이용한 변환
                .toList();
    }

    // 3. 알림 읽음 처리 (프론트 markAsRead 대응)
    @PatchMapping("/api/notifications/{id}/read")
    public void readNotification(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}