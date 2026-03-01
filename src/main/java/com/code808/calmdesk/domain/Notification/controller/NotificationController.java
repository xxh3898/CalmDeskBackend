package com.code808.calmdesk.domain.Notification.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.code808.calmdesk.domain.Notification.dto.NotificationResponseDto;
import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import com.code808.calmdesk.domain.Notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification", description = "실시간 알림(SSE) 및 알림 내역 관리 API")
@RestController
@RequiredArgsConstructor // 리액트 포트 허용
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Operation(summary = "알림 구독 (SSE)", description = "Server-Sent Events(SSE)를 통해 실시간 알림을 구독합니다.")
    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@Parameter(description = "사용자 ID") @PathVariable Long memberId) {
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
    @Operation(summary = "알림 내역 조회", description = "사용자의 최근 알림 내역을 조회합니다.")
    @GetMapping("/api/notifications/{memberId}")
    public Page<NotificationResponseDto> getNotifications(
            @Parameter(description = "사용자 ID") @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 1. 리포지토리에서 엔티티 페이지를 가져옵니다.
        Page<Notification> notifications = notificationRepository.findAllByMemberIdOrderByCreateDateDesc(memberId, PageRequest.of(page, size));

        // 2. 엔티티 페이지를 DTO 페이지로 변환해서 반환합니다.
        return notifications.map(NotificationResponseDto::new);
    }

    // 3. 알림 읽음 처리 (프론트 markAsRead 대응)
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/api/notifications/{id}/read")
    public void readNotification(@Parameter(description = "알림 ID") @PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/api/notifications/read-all/{memberId}")
    public void readAllNotifications(@Parameter(description = "사용자 ID") @PathVariable Long memberId) {

        notificationService.markAllAsRead(memberId);

    }

}
