package com.code808.calmdesk.domain.attendance.service;

import com.code808.calmdesk.domain.attendance.dto.StressDto;

public interface StressSummaryService {
    StressDto.SummaryResponse createDailySummary(StressDto.SummaryRequest request);
}
