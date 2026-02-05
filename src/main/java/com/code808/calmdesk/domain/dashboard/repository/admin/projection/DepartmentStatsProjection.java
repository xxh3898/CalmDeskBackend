package com.code808.calmdesk.domain.dashboard.repository.admin.projection;

public interface DepartmentStatsProjection {
    Long getDepartmentId();
    String getDepartmentName();
    Double getAvgStressLevel();
    Long getMemberCount();
    Long getCooldownCount();
}
