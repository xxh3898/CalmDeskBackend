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
    // SSE 연결 객체를 저장하는 저장소
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 1. SSE 구독 저장 (트랜잭션 절대 금지)
    public void addEmitter(Long memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);

        // 연결 종료 시 Map에서 삭제하도록 설정
        emitter.onCompletion(() -> removeEmitter(memberId));
        emitter.onTimeout(() -> removeEmitter(memberId));
        emitter.onError((e) -> removeEmitter(memberId));
    }

    // 2. 에미터 제거 메서드 (안전하게 분리)
    public void removeEmitter(Long memberId) {
        emitters.remove(memberId);
    }

    // 3. 알림 전송 (EventListener에서 호출)
    public void send(Long memberId, Object data) {
        SseEmitter emitter = emitters.get(memberId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
            } catch (IOException e) {
                removeEmitter(memberId); // 전송 실패 시 연결 제거
            }
        }
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(Notification::markAsRead);
    }

    @Transactional
    public void markAllAsRead(Long memberId) {
        notificationRepository.markAllAsRead(memberId);
    }
}