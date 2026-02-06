package com.code808.calmdesk.domain.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.attendance.entity.CoolDown;

public interface CoolDownRepository extends JpaRepository<CoolDown, Long> {

    long countByMember_MemberId(Long memberId);

    long countByCreatedDateBetweenAndMember_Company_CompanyId(java.time.LocalDateTime start, java.time.LocalDateTime end, Long companyId);
}
