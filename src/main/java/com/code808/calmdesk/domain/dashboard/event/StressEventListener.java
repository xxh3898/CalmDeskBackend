package com.code808.calmdesk.domain.dashboard.event;

import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import com.code808.calmdesk.domain.dashboard.sse.SseEmitterRegistry;
import com.code808.calmdesk.domain.attendance.event.StressEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class StressEventListener {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final DashboardService dashboardService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStressUpdated(StressEvent event){
        Long companyId = event.getCompanyId();

        try{
            DashboardDto.DashboardRequest request = DashboardDto.DashboardRequest.builder()
                    .companyId(companyId)
                    .date(event.getSummaryDate())
                    .build();

            DashboardDto.CompanyStats companyStats = dashboardService.getCompanyStats(request);
            List<DashboardDto.DepartmentStats> departmentStats = dashboardService.getDepartmentStats(request);
            List<DashboardDto.HighRiskMember> highRiskMembers = dashboardService.getHighRiskMembers(request);

            sseEmitterRegistry.sendToCompany(companyId, "companyStats", companyStats);
            sseEmitterRegistry.sendToCompany(companyId, "departmentStats", departmentStats);
            sseEmitterRegistry.sendToCompany(companyId, "highRiskMembers", highRiskMembers);

        }catch(Exception e){
            log.error("SSE 푸시 실패 - companyId : {}", companyId, e);
        }
    }
}
