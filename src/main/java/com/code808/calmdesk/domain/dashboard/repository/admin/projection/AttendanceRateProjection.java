package com.code808.calmdesk.domain.dashboard.repository.admin.projection;

public interface AttendanceRateProjection {
    Long getAttendCount();
    Long getTotalMemberCount();

    default Double getAttendanceRate() {
        if (getTotalMemberCount() == null || getTotalMemberCount() == 0) {
            return 0.0;
        }
        Long count = (getAttendCount() != null) ? getAttendCount() : 0L;
        return (count.doubleValue() / getTotalMemberCount().doubleValue()) * 100.0;
    }
}
