package com.code808.calmdesk.domain.dashboard.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class SseEmitterRegistry {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    public SseEmitter register(Long companyId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);

        emitters.computeIfAbsent(companyId, key -> new CopyOnWriteArrayList<>()).add(emitter);
        log.info("SSE 연결 등록 - companyId: {}, 현재 연결 수: {}", companyId, emitters.get(companyId).size());

        emitter.onCompletion(() -> remove(companyId, emitter));
        emitter.onTimeout(() -> remove(companyId, emitter));
        emitter.onError(e -> remove(companyId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            log.warn("SSE 초기 연결 이벤트 전송 실패 - companyId: {}", companyId);
            remove(companyId, emitter);
        }

        return emitter;
    }

    public void sendToCompany(Long companyId, String eventName, Object data) {
        List<SseEmitter> companyEmitters = emitters.get(companyId);
        if (companyEmitters == null || companyEmitters.isEmpty()) {
            log.debug("SSE 전송 대상 없음 - companyId: {}", companyId);
            return;
        }

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        companyEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                log.warn("SSE 이벤트 전송 실패 - companyId: {}", companyId);
                deadEmitters.add(emitter);
            }
        });

        deadEmitters.forEach(emitter -> remove(companyId, emitter));
    }

    @Scheduled(fixedDelay = 30000)
    public void sendHeartbeat() {
        emitters.forEach((companyId, emitterList) -> {
            List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
            emitterList.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    log.warn("SSE heartbeat 전송 실패 - companyId: {}", companyId);
                    deadEmitters.add(emitter);
                }
            });
            deadEmitters.forEach(emitter -> remove(companyId, emitter));
        });
    }

    private void remove(Long companyId, SseEmitter emitter) {
        List<SseEmitter> companyEmitters = emitters.get(companyId);
        if (companyEmitters != null) {
            companyEmitters.remove(emitter);
            log.info("SSE 연결 해제 - companyId: {}, 남은 연결 수: {}", companyId, companyEmitters.size());

            // 해당 회사의 연결이 아무도 없으면 키도 제거
            if (companyEmitters.isEmpty()) {
                emitters.remove(companyId);
            }
        }
    }
}