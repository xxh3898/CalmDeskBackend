package com.code808.calmdesk.domain.dashboard.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.code808.calmdesk.domain.attendance.event.DashboardEvent;
import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import com.code808.calmdesk.domain.dashboard.sse.SseEmitterRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardEventListener {

    private final SseEmitterRegistry sseEmitterRegistry;
    private final DashboardService dashboardService;

    // 회사별 마지막 푸시 시간을 저장하여 너무 잦은 업데이트 방지 (5초 제한)
    private final Map<Long, Long> lastPushMap = new ConcurrentHashMap<>();
    private static final long MIN_PUSH_INTERVAL_MS = 5000;

    @Async // 비동기로 실행하여 메인 로직(출퇴근 등)의 응답 속도에 영향을 주지 않음
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDashboardUpdated(DashboardEvent event) {
        Long companyId = event.getCompanyId();
        long now = System.currentTimeMillis();

        // 마지막 푸시 이후 5초가 지나지 않았으면 스킵
        if (now - lastPushMap.getOrDefault(companyId, 0L) < MIN_PUSH_INTERVAL_MS) {
            log.debug("대시보드 푸시 스킵 (너무 잦은 요청) - companyId: {}", companyId);
            return;
        }

        lastPushMap.put(companyId, now);
        push(companyId);
    }

    private void push(Long companyId) {
        try {
            DashboardDto.DashboardRequest request = new DashboardDto.DashboardRequest();
            request.setCompanyId(companyId);
            DashboardDto.DashboardResponse stats = dashboardService.getAllStats(request);
            sseEmitterRegistry.sendToCompany(companyId, "dashboard", stats);
            log.info("대시보드 push 완료 - companyId: {}", companyId);
        } catch (Exception e) {
            log.error("SSE 푸시 실패 - companyId: {}", companyId, e);
        }
    }

}
