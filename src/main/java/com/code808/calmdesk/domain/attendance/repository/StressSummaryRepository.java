package com.code808.calmdesk.domain.attendance.repository;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StressSummaryRepository extends JpaRepository<StressSummary, Long> {

    /**
     * 회원의 가장 최근 스트레스 요약 조회 (주간 요약 등)
     */
    @Query("SELECT s FROM STRESS_SUMMARY s WHERE s.member.memberId = :memberId ORDER BY s.endTime DESC LIMIT 1")
    Optional<StressSummary> findLatestByMemberId(@Param("memberId") Long memberId);

    /**
     * 회원의 특정 기간 타입 스트레스 요약 조회
     */
    @Query("SELECT s FROM STRESS_SUMMARY s WHERE s.member.memberId = :memberId AND s.period = :period ORDER BY s.endTime DESC LIMIT 1")
    Optional<StressSummary> findLatestByMemberIdAndPeriod(@Param("memberId") Long memberId, @Param("period") String period);
}
