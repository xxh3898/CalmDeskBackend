package com.code808.calmdesk.domain.vacation.repository;

import com.code808.calmdesk.domain.vacation.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

    @Query("SELECT v FROM Vacation v WHERE v.requestMember.memberId = :memberId ORDER BY v.startDate DESC")
    List<Vacation> findByRequestMember(@Param("memberId") Long memberId);

    /** 해당 기간과 겹치는 휴가 조회 (기간 겹침 조건) */
    @Query("SELECT v FROM Vacation v WHERE v.requestMember.memberId = :memberId " +
           "AND v.endDate >= :rangeStart AND v.startDate < :rangeEnd")
    List<Vacation> findByRequestMemberAndDateOverlap(@Param("memberId") Long memberId,
                                                     @Param("rangeStart") LocalDateTime rangeStart,
                                                     @Param("rangeEnd") LocalDateTime rangeEnd);
}
