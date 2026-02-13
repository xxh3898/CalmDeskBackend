package com.code808.calmdesk.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;

import javax.swing.text.html.Option;

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

    @Query("SELECT m FROM MEMBER m "
            + "LEFT JOIN FETCH m.department "
            + "LEFT JOIN FETCH m.rank "
            + "WHERE m.company.companyId = :companyId")
    List<Member> findAllByCompanyIdWithDepartmentAndRank(@Param("companyId") Long companyId);

    long countByRegisterDateBefore(java.time.LocalDate date);

    long countByJoinDateBefore(java.time.LocalDate date);

    long countByJoinDateBetween(java.time.LocalDate start, java.time.LocalDate end);

    @Query("SELECT m FROM MEMBER m LEFT JOIN FETCH m.department LEFT JOIN FETCH m.rank WHERE m.company.companyId = :companyId AND m.status = :status")
    List<Member> findByCompany_CompanyIdAndStatusWithDetails(@Param("companyId") Long companyId, @Param("status") CommonEnums.Status status);

    @Query("SELECT m FROM MEMBER m LEFT JOIN FETCH m.department LEFT JOIN FETCH m.rank WHERE m.company.companyId = :companyId AND m.status IN :statuses ORDER BY m.createdDate DESC")
    List<Member> findByCompany_CompanyIdAndStatusInWithDetails(@Param("companyId") Long companyId, @Param("statuses") List<CommonEnums.Status> statuses);

    @Query("SELECT m.company.companyId FROM MEMBER m WHERE m.email = :email")
    Optional<Long> findCompanyIdByEmail(@Param("email") String email);

    long countByCompany_CompanyId(Long companyId);

    long countByCompany_CompanyIdAndJoinDateBefore(Long companyId, java.time.LocalDate date);

    // MemberRepository.java
    List<Member> findAllByCompany_CompanyIdAndRole(Long companyId, Member.Role role);

}
