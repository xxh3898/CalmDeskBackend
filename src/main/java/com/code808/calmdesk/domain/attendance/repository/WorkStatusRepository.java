package com.code808.calmdesk.domain.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.attendance.entity.WorkStatus;
import com.code808.calmdesk.domain.member.entity.Member;

public interface WorkStatusRepository extends JpaRepository<WorkStatus, Long> {

    Optional<WorkStatus> findByMember(Member member);

    List<WorkStatus> findByMemberIn(List<Member> members);
}
