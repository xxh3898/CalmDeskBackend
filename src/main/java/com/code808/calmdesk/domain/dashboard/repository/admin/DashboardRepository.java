package com.code808.calmdesk.domain.dashboard.repository.admin;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.DepartmentStatsProjection;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.CompanyStatsProjection;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.AttendanceRateProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<StressSummary, Long> {
        // Optional<StressSummary> findByMember_MemberIdAndSummaryDate(Long memberId,
        // LocalDate summaryDate);

        @Query("SELECT d.departmentId as departmentId, " +
                        "d.departmentName as departmentName, " +
                        "AVG(s.avgStressLevel) as avgStressLevel, " +
                        "COUNT(s) as memberCount, " +
                        "COALESCE((SELECT COUNT(c) FROM COOLDOWN c " +
                        " WHERE c.member.department = d " +
                        " AND c.createdDate BETWEEN :startDate AND :endDate), 0) as cooldownCount " +
                        "FROM StressSummary s " +
                        "JOIN s.department d " +
                        "WHERE d.company.companyId = :companyId " +
                        "AND s.summaryDate = :date " +
                        "GROUP BY d.departmentId, d.departmentName")
        List<DepartmentStatsProjection> findDepartmentStats(
                        @Param("companyId") Long companyId,
                        @Param("date") LocalDate date,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT s FROM StressSummary s " +
                        "JOIN FETCH s.member m " +
                        "JOIN FETCH s.department d " +
                        "WHERE m.company.companyId = :companyId " +
                        "AND s.summaryDate = :date " +
                        "AND s.avgStressLevel >= :threshold " +
                        "ORDER BY s.avgStressLevel DESC")
        List<StressSummary> findHighRiskMembers(
                        @Param("companyId") Long companyId,
                        @Param("date") LocalDate date,
                        @Param("threshold") Integer threshold,
                        Pageable pageable);

        @Query("SELECT AVG(s.avgStressLevel) " +
                        "FROM StressSummary s " +
                        "JOIN s.member m " +
                        "WHERE m.company.companyId = :companyId " +
                        "AND s.summaryDate = :date")
        Double findCompanyAvgStress(
                        @Param("companyId") Long companyId,
                        @Param("date") LocalDate date);

        @Query("SELECT AVG(s.avgStressLevel) as avgStressLevel, " +
                        "COUNT(s.summaryId) as totalMembers, " +
                        "SUM(CASE WHEN s.avgStressLevel >= :threshold THEN 1 ELSE 0 END) as highRiskCount " +
                        "FROM StressSummary s " +
                        "WHERE s.member.company.companyId = :companyId " +
                        "AND s.summaryDate = :date")
        CompanyStatsProjection findCompanyStats(
                        @Param("companyId") Long companyId,
                        @Param("date") LocalDate date,
                        @Param("threshold") Integer threshold);

        @Query("SELECT " +
                        "SUM(CASE WHEN a.attendanceStatus IN :statuses THEN 1 ELSE 0 END) as attendCount, " +
                        "(SELECT COUNT(m2.memberId) FROM MEMBER m2 WHERE m2.company.companyId = :companyId) as totalMemberCount "
                        +
                        "FROM MEMBER m " +
                        "LEFT JOIN Attendance a ON a.member.memberId = m.memberId AND a.workDate = :date " +
                        "WHERE m.company.companyId = :companyId")
        AttendanceRateProjection findAttendanceRate(
                        @Param("companyId") Long companyId,
                        @Param("date") LocalDate date,
                        @Param("statuses") List<Attendance.AttendanceStatus> statuses);

        @Query("SELECT COUNT(c.counselionId) " +
                        "FROM Consultation c " +
                        "WHERE c.member.company.companyId = :companyId " +
                        "AND c.status = 'WAITING'")
        Long countWaitingConsultations(@Param("companyId") Long companyId);

        @Query("SELECT COUNT(v.vacationId) " +
                        "FROM Vacation v " +
                        "WHERE v.requestMember.company.companyId = :companyId " +
                        "AND v.status = 'N'")
        Long countPendingVacations(@Param("companyId") Long companyId);

        @Query("SELECT COUNT(c) FROM COOLDOWN c " +
                        "JOIN c.member m " +
                        "WHERE m.company.companyId = :companyId " +
                        "AND c.createdDate BETWEEN :startDate AND :endDate")
        Long testCoolDownCount(
                        @Param("companyId") Long companyId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
