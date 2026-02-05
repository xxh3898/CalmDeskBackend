package com.code808.calmdesk.domain.dashboard.service.admin;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.repository.admin.DashboardRepository;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.AttendanceRateProjection;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.CompanyStatsProjection;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.DepartmentStatsProjection;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
        private final DashboardRepository dashboardRepository;
        private final AttendanceRepository attendanceRepository;

        private LocalDate getBaseDate(DashboardDto.DashboardRequest request) {
                return Optional.ofNullable(request.getDate())
                                .or(() -> attendanceRepository.findLatestWorkDate(request.getCompanyId()))
                                .orElseGet(LocalDate::now);
        }

        public List<DashboardDto.DepartmentStats> getDepartmentStats(DashboardDto.DashboardRequest request) {
                LocalDate baseDate = getBaseDate(request);
                LocalDateTime startOfDay = baseDate.atStartOfDay();
                LocalDateTime endOfDay = baseDate.atTime(23, 59, 59);

                List<DepartmentStatsProjection> projections = dashboardRepository
                                .findDepartmentStats(request.getCompanyId(), baseDate, startOfDay, endOfDay);

                Long totalCoolDown = dashboardRepository.testCoolDownCount(
                                request.getCompanyId(), startOfDay, endOfDay);

                return projections.stream()
                                .map(DashboardDto.DepartmentStats::of)
                                .toList();
        }

        public List<DashboardDto.HighRiskMember> getHighRiskMembers(DashboardDto.DashboardRequest request) {
                Pageable top5 = PageRequest.of(0, 5);
                LocalDate baseDate = getBaseDate(request);
                List<StressSummary> highRiskMembers = dashboardRepository.findHighRiskMembers(
                                request.getCompanyId(),
                                baseDate,
                                request.getThreshold(),
                                top5);
                return highRiskMembers.stream()
                                .map(DashboardDto.HighRiskMember::of)
                                .toList();
        }

        public DashboardDto.CompanyStats getCompanyStats(DashboardDto.DashboardRequest request) {
                LocalDate baseDate = getBaseDate(request);

                CompanyStatsProjection todayCompanyStats = dashboardRepository.findCompanyStats(
                                request.getCompanyId(), baseDate, request.getThreshold());

                Double yesterdayAvg = dashboardRepository.findCompanyAvgStress(
                                request.getCompanyId(), baseDate.minusDays(1));

                List<Attendance.AttendanceStatus> attendanceStatuses = Arrays.asList(
                                Attendance.AttendanceStatus.ATTEND,
                                Attendance.AttendanceStatus.LATE);

                AttendanceRateProjection todayAttendance = dashboardRepository.findAttendanceRate(
                                request.getCompanyId(), baseDate, attendanceStatuses);

                AttendanceRateProjection yesterdayAttendance = dashboardRepository.findAttendanceRate(
                                request.getCompanyId(), baseDate.minusDays(1), attendanceStatuses);

                Long consultationCount = dashboardRepository.countWaitingConsultations(request.getCompanyId());
                Long vacationCount = dashboardRepository.countPendingVacations(request.getCompanyId());

                return DashboardDto.CompanyStats.of(
                                todayCompanyStats.getAvgStressLevel(),
                                yesterdayAvg,
                                todayCompanyStats.getTotalMembers(),
                                todayCompanyStats.getHighRiskCount(),
                                (todayAttendance != null) ? todayAttendance.getAttendanceRate() : 0.0,
                                (yesterdayAttendance != null) ? yesterdayAttendance.getAttendanceRate() : 0.0,
                                consultationCount,
                                vacationCount);
        }

        public DashboardDto.DashboardResponse getAllStats(DashboardDto.DashboardRequest request) {
                DashboardDto.CompanyStats companyStats = getCompanyStats(request);
                List<DashboardDto.DepartmentStats> departmentStats = getDepartmentStats(request);
                List<DashboardDto.HighRiskMember> highRiskMembers = getHighRiskMembers(request);

                return DashboardDto.DashboardResponse.of(
                                request.getDate(),
                                companyStats,
                                departmentStats,
                                highRiskMembers);
        }

}