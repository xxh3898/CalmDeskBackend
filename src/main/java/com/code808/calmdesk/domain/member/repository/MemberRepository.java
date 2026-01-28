package com.code808.calmdesk.domain.member.repository;

import java.util.List;
import java.util.Optional;

import com.code808.calmdesk.domain.company.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findByDepartment(Department department);
}
