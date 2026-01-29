package com.code808.calmdesk.domain.vacation.repository;

import com.code808.calmdesk.domain.vacation.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

    @Query("SELECT v FROM Vacation v WHERE v.requestMember.memberId = :memberId ORDER BY v.startDate DESC")
    List<Vacation> findByRequestMember(@Param("memberId") Long memberId);
}
