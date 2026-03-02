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

    private static final List<Attendance.AttendanceStatus> attendanceStatuses = Arrays.asList(
            Attendance.AttendanceStatus.ATTEND,
            Attendance.AttendanceStatus.LATE);

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

        CompanyStatsProjection todayStats = dashboardRepository.findCompanyStats(
                request.getCompanyId(), baseDate, request.getThreshold());

        AttendanceRateProjection todayAttendance = dashboardRepository.findAttendanceRate(
                request.getCompanyId(), baseDate, attendanceStatuses);

        Long consultationCount = dashboardRepository.countWaitingConsultations(request.getCompanyId());
        Long vacationCount = dashboardRepository.countPendingVacations(request.getCompanyId());

        return DashboardDto.CompanyStats.of(
                todayStats.getAvgStressLevel(),
                todayStats.getTotalMembers(),
                todayStats.getHighRiskCount(),
                (todayAttendance != null) ? todayAttendance.getAttendanceRate() : 0.0,
                consultationCount,
                vacationCount);
    }

//    public DashboardDto.YesterdayStats getYesterdayStats(DashboardDto.DashboardRequest request) {
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//
//        Double yesterdayAvg = dashboardRepository.findCompanyAvgStress(
//                request.getCompanyId(), yesterday);
//
//        AttendanceRateProjection yesterdayAttendance = dashboardRepository.findAttendanceRate(
//                request.getCompanyId(), yesterday, attendanceStatuses);
//
//        return DashboardDto.YesterdayStats.of(
//                yesterday,
//                yesterdayAvg,
//                (yesterdayAttendance != null) ? yesterdayAttendance.getAttendanceRate() : 0.0);
//    }
    public DashboardDto.DashboardResponse getAllStats(DashboardDto.DashboardRequest request) {
        log.info("==== [대시보드 조회 시작] ====");
        log.info("1. 요청된 날짜(Request Date): {}", request.getDate());
        log.info("3. 회사 ID: {}", request.getCompanyId());
        log.info("4. 위험군 임계치(Threshold): {}", request.getThreshold());
        log.info("=============================");

        DashboardDto.CompanyStats companyStats = getCompanyStats(request);
        List<DashboardDto.DepartmentStats> departmentStats = getDepartmentStats(request);
        List<DashboardDto.HighRiskMember> highRiskMembers = getHighRiskMembers(request);

        log.info("==== [대시보드 조회 종료] ====");
        return DashboardDto.DashboardResponse.of(
                request.getDate(),
                companyStats,
                departmentStats,
                highRiskMembers);
    }

}
