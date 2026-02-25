package com.code808.calmdesk.domain.monitoring.service;

import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;

public interface AdminMonitoringService {

    MonitoringDto getMonitoringData(String period, Integer year, Long companyId);

    byte[] generateExcelReport(String period, Integer year, Long companyId);
}
