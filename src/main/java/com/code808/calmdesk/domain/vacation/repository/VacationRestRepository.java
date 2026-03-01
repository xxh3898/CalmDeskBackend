package com.code808.calmdesk.domain.vacation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.vacation.entity.VacationRest;

public interface VacationRestRepository extends JpaRepository<VacationRest, Long> {

    List<VacationRest> findByMember_MemberIdIn(List<Long> memberIds);
}
