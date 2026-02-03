package com.code808.calmdesk.domain.vacation.repository;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

    @Query("SELECT v FROM Vacation v WHERE v.requestMember.memberId = :memberId ORDER BY v.startDate DESC")
    List<Vacation> findByRequestMember(@Param("memberId") Long memberId);
    @Query("SELECT vr FROM VacationRest vr WHERE vr.member.memberId = :memberId")
    Optional<VacationRest> findByMemberId(@Param("memberId") Long memberId);
    @Query("SELECT v FROM Vacation v JOIN FETCH v.requestMember m JOIN FETCH m.department WHERE m.company.companyId = :companyId ORDER BY v.startDate DESC")
    List<Vacation> findByRequestMember_Company_CompanyIdOrderByStartDateDesc(@Param("companyId") Long companyId);

    /**
     * 날짜 범위가 겹치는 휴가 조회 (대기 중이거나 승인된 휴가만)
     * 반려된 휴가는 제외
     */
    @Query("SELECT v FROM Vacation v WHERE v.requestMember.memberId = :memberId " +
            "AND v.status IN :statuses " +
            "AND ((v.startDate <= :endDate AND v.endDate >= :startDate))")
    List<Vacation> findOverlappingVacations(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<CommonEnums.Status> statuses
    );
}
