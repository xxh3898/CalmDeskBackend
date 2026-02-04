package com.code808.calmdesk.domain.dashboard.service.admin;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.repository.admin.DashboardRepository;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.CompanyStatsProjection;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.DepartmentStatsProjection;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository dashboardRepository;

    public List<DashboardDto.DepartmentStats> getDepartmentStats(DashboardDto.DashboardRequest request) {
        List<DepartmentStatsProjection> projections = dashboardRepository
                .findDepartmentStats(request.getCompanyId(), request.getDate());
        return projections.stream()
                .map(DashboardDto.DepartmentStats::of)
                .toList();
    }

    public List<DashboardDto.HighRiskMember> getHighRiskMembers(DashboardDto.DashboardRequest request) {
        Pageable top5 = PageRequest.of(0,5);
        List<StressSummary> highRiskMembers = dashboardRepository.findHighRiskMembers(
                request.getCompanyId(),
                request.getDate(),
                top5
        );
        return highRiskMembers.stream()
                .map(DashboardDto.HighRiskMember::of)
                .toList();
    }

    public DashboardDto.CompanyStats getCompanyStats(DashboardDto.DashboardRequest request) {
        CompanyStatsProjection todayComapnyStats = dashboardRepository.findCompanyStats(
                request.getCompanyId(),request.getDate(),request.getThreshold()
        );

        Double yesterdayAvg = dashboardRepository.findCompanyAvgStress(
                request.getCompanyId(),request.getDate().minusDays(1)
        );

        return DashboardDto.CompanyStats.of(
                todayComapnyStats.getAvgStressLevel(),
                yesterdayAvg,
                todayComapnyStats.getTotalMembers(),
                todayComapnyStats.getHighRiskCount()
        );
    }


    public DashboardDto.DashboardResponse getAllStats(DashboardDto.DashboardRequest request) {
        DashboardDto.CompanyStats companyStats = getCompanyStats(request);
        List<DashboardDto.DepartmentStats> departmentStats = getDepartmentStats(request);
        List<DashboardDto.HighRiskMember> highRiskMembers = getHighRiskMembers(request);

        return DashboardDto.DashboardResponse.of(
                request.getDate(),
                companyStats,
                departmentStats,
                highRiskMembers
        );
    }
}