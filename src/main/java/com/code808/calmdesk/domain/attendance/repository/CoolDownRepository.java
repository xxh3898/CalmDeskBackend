package com.code808.calmdesk.domain.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.attendance.entity.CoolDown;

public interface CoolDownRepository extends JpaRepository<CoolDown, Long> {

    long countByMember_MemberId(Long memberId);

    long countByCreatedDateBetweenAndMember_Company_CompanyId(java.time.LocalDateTime start, java.time.LocalDateTime end, Long companyId);

    @Query("SELECT c.member.memberId, COUNT(c) FROM COOLDOWN c WHERE c.member.memberId IN :memberIds GROUP BY c.member.memberId")
    List<Object[]> countByMemberIds(@Param("memberIds") List<Long> memberIds);
}
