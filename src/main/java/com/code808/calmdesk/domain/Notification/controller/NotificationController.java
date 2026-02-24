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

    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long memberId) {
        // 1. 타임아웃 설정 (1시간)
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);

        // 2. 서비스에 등록 (이 메서드 내부에서 @Transactional이 없는지 꼭 확인하세요!)
        notificationService.addEmitter(memberId, emitter);

        // 3. 연결 종료/타임아웃 콜백 설정 (메모리 누수 방지 핵심)
        emitter.onCompletion(() -> notificationService.removeEmitter(memberId));
        emitter.onTimeout(() -> notificationService.removeEmitter(memberId));
        emitter.onError((e) -> notificationService.removeEmitter(memberId));

        // 4. 더미 데이터 전송 (503 에러 및 연결 유지 방지)
        try {
            emitter.send(SseEmitter.event()
                    .id("") // Last-Event-ID 대응을 위해 빈 값이라도 넣어주는 것이 좋습니다.
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            notificationService.removeEmitter(memberId);
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


    @PatchMapping("/api/notifications/read-all/{memberId}")
    public void readAllNotifications(@PathVariable Long memberId) {

        notificationService.markAllAsRead(memberId);

    }


}