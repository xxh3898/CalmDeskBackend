package com.code808.calmdesk.domain.attendance.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.attendance.entity.StressFactor;

@Repository
public interface StressFactorRepository extends JpaRepository<StressFactor, Long> {

    // 기간 내 가장 빈도수가 높은 스트레스 요인 집계
    @Query("SELECT sf.category, COUNT(sf) "
            + "FROM StressFactor sf "
            + "WHERE sf.emotionCheckin.createdDate BETWEEN :startDate AND :endDate "
            + "GROUP BY sf.category "
            + "ORDER BY COUNT(sf) DESC")
    List<Object[]> findTopStressFactors(LocalDateTime startDate, LocalDateTime endDate);
}
