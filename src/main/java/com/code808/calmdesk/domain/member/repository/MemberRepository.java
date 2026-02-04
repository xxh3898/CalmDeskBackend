package com.code808.calmdesk.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM MEMBER m "
            + "LEFT JOIN FETCH m.company "
            + "LEFT JOIN FETCH m.department "
            + "WHERE m.email = :email")
    Optional<Member> findEmailWithDetails(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT m FROM MEMBER m "
            + "LEFT JOIN FETCH m.company "
            + "LEFT JOIN FETCH m.department "
            + "LEFT JOIN FETCH m.rank "
            + "WHERE m.memberId = :memberId")
    Optional<Member> findByIdWithCompanyAndDepartmentAndRank(@Param("memberId") Long memberId);

    List<Member> findByDepartment(Department department);

    long countByRegisterDateBefore(java.time.LocalDate date);
}
