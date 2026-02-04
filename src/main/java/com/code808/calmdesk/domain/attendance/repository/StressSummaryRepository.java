package com.code808.calmdesk.domain.attendance.repository;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StressSummaryRepository extends JpaRepository<StressSummary, Long> {

    @Query("SELECT s FROM StressSummary s WHERE s.member.memberId = :memberId ORDER BY s.summaryDate DESC")
    Optional<StressSummary> findLatestByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT s FROM StressSummary s WHERE s.member.memberId = :memberId ORDER BY s.summaryDate DESC")
    Optional<StressSummary> findLatestByMemberIdAndPeriod(@Param("memberId") Long memberId, @Param("period") String period);
}
