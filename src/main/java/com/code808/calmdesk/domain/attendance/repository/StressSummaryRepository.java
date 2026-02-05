package com.code808.calmdesk.domain.attendance.repository;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.company.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface StressSummaryRepository extends JpaRepository<StressSummary, Long> {

    Optional<StressSummary> findByMember_MemberIdAndSummaryDate(
            Long memberId,
            LocalDate summaryDate
    );

    boolean existsByMember_MemberIdAndSummaryDate(
            Long memberId,
            LocalDate summaryDate
    );

    /**
     * 가장 최근 요약 (여러 건 있어도 summaryDate 기준으로 가장 최신 1건만)
     */
    Optional<StressSummary> findTopByMember_MemberIdOrderBySummaryDateDesc(Long memberId);

    // 기간 내 모든 스트레스 요약 조회
    List<StressSummary> findBySummaryDateBetween(LocalDate startDate, LocalDate endDate);

    // 특정 부서의 기간 내 평균 스트레스 조회
    @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s WHERE s.department = :department AND s.summaryDate BETWEEN :startDate AND :endDate")
    Double findAvgStressByDepartmentAndDateRange(@Param("department") Department department, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 전체 기간 내 평균 스트레스
    @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s WHERE s.summaryDate BETWEEN :startDate AND :endDate")
    Double findAvgStressByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
