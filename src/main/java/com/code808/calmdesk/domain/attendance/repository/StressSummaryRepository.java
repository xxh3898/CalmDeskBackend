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

        /**
         * 가장 최근 요약 (여러 건 있어도 summaryDate 기준으로 가장 최신 1건만)
         */
        Optional<StressSummary> findTopByMember_MemberIdOrderBySummaryDateDesc(Long memberId);

        @Query("SELECT AVG(s.avgStressLevel) FROM StressSummary s JOIN s.member m WHERE m.company.companyId = :companyId AND s.summaryDate BETWEEN :startDate AND :endDate")
        Double findAvgStressByDateRangeAndCompany(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

        // 부서별 통계 (부서명, 평균 점수, 고위험군 멤버 수)
        // 멤버별 기간 평균을 먼저 구한 뒤, 그 평균이 4.0 이상인 멤버만 고위험군으로 집계
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

        // 점수 구간별 카운트 (메모리 최적화)
        // scoreCondition 예: >= 4.0 (위험), < 3.0 (정상) 등은 메서드를 따로 만드는 게 안전함
        // 고위험군 (멤버별 기간 평균 >= 4.0 → 70점 이상)
        @Query("""
                        SELECT COUNT(DISTINCT s.member.memberId)
                        FROM StressSummary s
                        JOIN s.member m
                        WHERE m.company.companyId = :companyId
                          AND s.summaryDate BETWEEN :startDate AND :endDate
                        GROUP BY s.member.memberId
                        HAVING AVG(s.avgStressLevel) >= 4.0
                        """)
        // HAVING 결과셋 행 수 = 위험군 멤버 수
        List<Long> findHighRiskMemberIdsByCompany(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

        default long countHighRiskByCompany(LocalDate startDate, LocalDate endDate, Long companyId) {
                return findHighRiskMemberIdsByCompany(startDate, endDate, companyId).size();
        }

        // 주의군 (멤버별 기간 평균 3.0 이상 4.0 미만 → 30 ~ 70점 미만)
        @Query("""
                        SELECT COUNT(DISTINCT s.member.memberId)
                        FROM StressSummary s
                        JOIN s.member m
                        WHERE m.company.companyId = :companyId
                          AND s.summaryDate BETWEEN :startDate AND :endDate
                        GROUP BY s.member.memberId
                        HAVING AVG(s.avgStressLevel) >= 3.0 AND AVG(s.avgStressLevel) < 4.0
                        """)
        List<Long> findCautionMemberIdsByCompany(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

        default long countCautionByCompany(LocalDate startDate, LocalDate endDate, Long companyId) {
                return findCautionMemberIdsByCompany(startDate, endDate, companyId).size();
        }

        // 정상군 (멤버별 기간 평균 < 3.0 → 30점 미만)
        @Query("""
                        SELECT COUNT(DISTINCT s.member.memberId)
                        FROM StressSummary s
                        JOIN s.member m
                        WHERE m.company.companyId = :companyId
                          AND s.summaryDate BETWEEN :startDate AND :endDate
                        GROUP BY s.member.memberId
                        HAVING AVG(s.avgStressLevel) < 3.0
                        """)
        List<Long> findNormalMemberIdsByCompany(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId);

        default long countNormalByCompany(LocalDate startDate, LocalDate endDate, Long companyId) {
                return findNormalMemberIdsByCompany(startDate, endDate, companyId).size();
        }

        /**
         * 회원별 기간 내 스트레스 요약 목록 (주간 집계용)
         */
        List<StressSummary> findByMember_MemberIdAndSummaryDateBetween(
                        Long memberId,
                        LocalDate startDate,
                        LocalDate endDate);
}
