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

    @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s JOIN s.member m WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate")
    Double findAvgStressByDateRangeAndCompany(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    // 부서별 통계 (부서명, 평균 점수, 고위험군 수)
    @Query("SELECT s.department.departmentName, AVG(s.avgStressLevel), "
            + "SUM(CASE WHEN s.avgStressLevel >= 4.0 THEN 1 ELSE 0 END) "
            + "FROM StressSummary s JOIN s.member m "
            + "WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate "
            + "GROUP BY s.department.departmentName")
    List<Object[]> findDeptStatsByCompany(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    // 점수 구간별 카운트 (메모리 최적화)
    // scoreCondition 예: >= 4.0 (위험), < 3.0 (정상) 등은 메서드를 따로 만드는 게 안전함
    // 고위험군 (4.0 이상 -> 70점 이상)
    @Query("SELECT COUNT(s) FROM StressSummary s JOIN s.member m WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate AND s.avgStressLevel >= 4.0")
    long countHighRiskByCompany(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    // 주의군 (3.0 이상 4.0 미만 -> 30점 ~ 70점 미만)
    @Query("SELECT COUNT(s) FROM StressSummary s JOIN s.member m WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate AND s.avgStressLevel >= 3.0 AND s.avgStressLevel < 4.0")
    long countCautionByCompany(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    // 정상군 (3.0 미만 -> 30점 미만)
    @Query("SELECT COUNT(s) FROM StressSummary s JOIN s.member m WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate AND s.avgStressLevel < 3.0")
    long countNormalByCompany(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

    /**
     * 회원별 기간 내 스트레스 요약 목록 (주간 집계용)
     */
    List<StressSummary> findByMember_MemberIdAndSummaryDateBetween(
            Long memberId,
            LocalDate startDate,
            LocalDate endDate
    );
}
