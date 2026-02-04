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

    // 1. 특정 날짜 데이터 조회 (기존 유지)
    Optional<StressSummary> findByMember_MemberIdAndSummaryDate(
            Long memberId,
            LocalDate summaryDate
    );

    // 2. 존재 여부 확인 (기존 유지)
    boolean existsByMember_MemberIdAndSummaryDate(
            Long memberId,
            LocalDate summaryDate
    );

    /**
     * ✅ [수정] 가장 최신 스트레스 요약 1건 조회
     * findTopBy: 결과가 여러 개라도 첫 번째 것만 가져옴
     * OrderBySummaryDateDesc: 날짜 최신순 정렬
     */
    Optional<StressSummary> findTopByMember_MemberIdOrderBySummaryDateDesc(Long memberId);

    /**
     * ✅ [수정] 기간별 최신 데이터 (기존 @Query 에러 방지용)
     * 파라미터가 유동적이라면 쿼리보다 메서드 이름을 활용하는 것이 안전합니다.
     */
    Optional<StressSummary> findTopByMember_MemberIdAndSummaryDateBetweenOrderBySummaryDateDesc(
            Long memberId, LocalDate startDate, LocalDate endDate);

    // 3. 기간 내 모든 스트레스 요약 조회 (기존 유지)
    List<StressSummary> findBySummaryDateBetween(LocalDate startDate, LocalDate endDate);

    // 4. 특정 부서 통계 (기존 유지)
    @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s WHERE s.department = :department AND s.summaryDate BETWEEN :startDate AND :endDate")
    Double findAvgStressByDepartmentAndDateRange(@Param("department") Department department, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 5. 전체 평균 통계 (기존 유지)
    @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s WHERE s.summaryDate BETWEEN :startDate AND :endDate")
    Double findAvgStressByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}