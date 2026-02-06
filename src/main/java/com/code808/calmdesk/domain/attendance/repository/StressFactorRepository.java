package com.code808.calmdesk.domain.attendance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.attendance.entity.StressFactor;

@Repository
public interface StressFactorRepository extends JpaRepository<StressFactor, Long> {

    // 기간 내 가장 빈도수가 높은 스트레스 요인 집계
    @Query("SELECT sf.category, COUNT(sf) "
            + "FROM StressFactor sf JOIN sf.emotionCheckin ec JOIN ec.attendance a JOIN a.member m "
            + "WHERE m.company.companyId = :companyId AND ec.createdDate BETWEEN :startDate AND :endDate "
            + "GROUP BY sf.category "
            + "ORDER BY COUNT(sf) DESC")
    List<Object[]> findTopStressFactorsByCompany(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("companyId") Long companyId);
}
