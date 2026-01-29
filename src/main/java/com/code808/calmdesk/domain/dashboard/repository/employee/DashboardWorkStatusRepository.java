package com.code808.calmdesk.domain.dashboard.repository.employee;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.dashboard.entity.DashboardWorkStatus;
import com.code808.calmdesk.domain.member.entity.Member;

public interface DashboardWorkStatusRepository extends JpaRepository<DashboardWorkStatus, Long> {

    Optional<DashboardWorkStatus> findByMember(Member member);
}
