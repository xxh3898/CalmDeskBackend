package com.code808.calmdesk.domain.dashboard.service.admin;

import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;

import java.util.List;

public interface DashboardService {
    List<DashboardDto.DepartmentStats> getDepartmentStats(DashboardDto.DashboardRequest request);
    List<DashboardDto.HighRiskMember> getHighRiskMembers(DashboardDto.DashboardRequest request);
    DashboardDto.CompanyStats getCompanyStats(DashboardDto.DashboardRequest request);
    DashboardDto.DashboardResponse getAllStats(DashboardDto.DashboardRequest request);
//    DashboardDto.YesterdayStats getYesterdayStats(DashboardDto.DashboardRequest request);
}
