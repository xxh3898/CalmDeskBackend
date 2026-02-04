package com.code808.calmdesk.domain.dashboard.repository.admin.projection;

public interface CompanyStatsProjection {
    Double getAvgStressLevel();
    Long getTotalMembers();
    Long getHighRiskCount();
}
