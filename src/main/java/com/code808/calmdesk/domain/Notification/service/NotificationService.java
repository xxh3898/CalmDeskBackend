package com.code808.calmdesk.domain.Notification.service;

import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 구독 저장
    public void addEmitter(Long memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);
        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
    }

    // 알림 전송 (EventListener 등에서 호출)
    // NotificationService.java 내 send 메서드 보완
    public void send(Long memberId, Object data) {
        SseEmitter emitter = emitters.get(memberId);
        System.out.println("알림 전송 시도 - 대상 ID: " + memberId);
        System.out.println("현재 연결된 에미터들: " + emitters.keySet());

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
                System.out.println("✅ SSE 전송 성공!");
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        } else {
            System.out.println("❌ 전송 실패: ID " + memberId + "에 해당하는 에미터를 찾을 수 없음");
        }
    }

    // 알림 읽음 처리 (API용)
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead(); // 엔티티에 상태 업데이트 메서드 필요
        });
    }

    @Transactional
    public void markAllAsRead(Long memberId) {

        notificationRepository.markAllAsRead(memberId);

    }
}