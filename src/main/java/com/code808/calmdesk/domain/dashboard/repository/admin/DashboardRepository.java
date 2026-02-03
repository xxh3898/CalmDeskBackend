package com.code808.calmdesk.domain.dashboard.repository.admin;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.dashboard.repository.admin.projection.DepartmentStatsProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<StressSummary, Long> {
    Optional<StressSummary> findByMember_MemberIdAndSummaryDate(Long memberId, LocalDate summaryDate);

    @Query("SELECT d.departmentId as departmentId, " +
            "d.departmentName as departmentName, " +
            "AVG(s.avgStressLevel) as avgStressLevel, " +
            "COUNT(s) as memberCount " +
            "FROM StressSummary s " +
            "JOIN s.department d " +
            "WHERE d.company.companyId = :companyId " +
            "AND s.summaryDate = :date " +
            "GROUP BY d.departmentId, d.departmentName")
    List<DepartmentStatsProjection> findDepartmentStats(
            @Param("companyId") Long companyId,
            @Param("date") LocalDate date
    );

    @Query("SELECT s FROM StressSummary s " +
            "JOIN FETCH s.member m " +
            "JOIN FETCH s.department d " +
            "WHERE m.company.companyId = :companyId " +
            "AND s.summaryDate = :date " +
            "ORDER BY s.avgStressLevel DESC")
    List<StressSummary> findHighRiskMembers(
            @Param("companyId") Long companyId,
            @Param("date") LocalDate date,
            Pageable pageable
    );

    @Query("SELECT AVG(s.avgStressLevel) " +
            "FROM StressSummary s " +
            "JOIN s.member m " +
            "WHERE m.company.companyId = :companyId " +
            "AND s.summaryDate = :date")
    Double findCompanyAvgStress(
            @Param("companyId") Long companyId,
            @Param("date") LocalDate date
    );
}
