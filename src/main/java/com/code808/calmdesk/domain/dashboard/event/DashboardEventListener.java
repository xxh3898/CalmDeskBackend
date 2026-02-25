package com.code808.calmdesk.domain.dashboard.event;

import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import com.code808.calmdesk.domain.dashboard.sse.SseEmitterRegistry;
import com.code808.calmdesk.domain.attendance.event.DashboardEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardEventListener {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final DashboardService dashboardService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDashboardUpdated(DashboardEvent event) {
        push(event.getCompanyId());
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
