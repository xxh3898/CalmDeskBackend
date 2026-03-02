package com.code808.calmdesk.domain.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;

@Repository
public interface StressSummaryRepository extends JpaRepository<StressSummary, Long> {

    Optional<StressSummary> findByMember_MemberIdAndSummaryDate(
            Long memberId,
            LocalDate summaryDate);

    boolean existsByMember_MemberIdAndSummaryDate(
            Long memberId,
            LocalDate summaryDate);

    Optional<StressSummary> findTopByMember_MemberIdOrderBySummaryDateDesc(Long memberId);

    @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s JOIN s.member m WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate")
    Double findAvgStressByDateRangeAndCompany(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    @Query("""
                        SELECT m.department.departmentName,
                                AVG(s.avgStressLevel),
                                SUM(CASE WHEN s.avgStressLevel >= 4.0 THEN 1L ELSE 0L END)
                        FROM StressSummary s
                        JOIN s.member m
                        WHERE m.company.companyId = :companyId
                          AND s.summaryDate BETWEEN :startDate AND :endDate
                        GROUP BY m.department.departmentName
                        """)
    List<Object[]> findDeptStatsByCompany(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    @Query("""
                        SELECT COUNT(m)
                        FROM MEMBER m
                        WHERE m.company.companyId = :companyId
                          AND (SELECT AVG(s.avgStressLevel)
                               FROM StressSummary s
                               WHERE s.member = m
                                 AND s.summaryDate BETWEEN :startDate AND :endDate) >= 4.0
                        """)
    long countHighRiskByCompany(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    @Query("""
                        SELECT COUNT(m)
                        FROM MEMBER m
                        WHERE m.company.companyId = :companyId
                          AND (SELECT AVG(s.avgStressLevel)
                               FROM StressSummary s
                               WHERE s.member = m
                                 AND s.summaryDate BETWEEN :startDate AND :endDate) >= 3.0
                          AND (SELECT AVG(s.avgStressLevel)
                               FROM StressSummary s
                               WHERE s.member = m
                                 AND s.summaryDate BETWEEN :startDate AND :endDate) < 4.0
                        """)
    long countCautionByCompany(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    @Query("""
                        SELECT COUNT(m)
                        FROM MEMBER m
                        WHERE m.company.companyId = :companyId
                          AND (SELECT AVG(s.avgStressLevel)
                               FROM StressSummary s
                               WHERE s.member = m
                                 AND s.summaryDate BETWEEN :startDate AND :endDate) < 3.0
                          AND EXISTS (SELECT 1 FROM StressSummary s2 WHERE s2.member = m AND s2.summaryDate BETWEEN :startDate AND :endDate)
                        """)
    long countNormalByCompany(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    List<StressSummary> findByMember_MemberIdAndSummaryDateBetween(
            Long memberId,
            LocalDate startDate,
            LocalDate endDate);

    @Query("SELECT s FROM StressSummary s WHERE s.summaryId IN "
            + "(SELECT MAX(s2.summaryId) FROM StressSummary s2 WHERE s2.member.memberId IN :memberIds GROUP BY s2.member.memberId)")
    List<StressSummary> findLatestByMemberIds(@Param("memberIds") List<Long> memberIds);

    List<StressSummary> findByMember_MemberIdInAndSummaryDate(List<Long> memberIds, LocalDate summaryDate);
}
