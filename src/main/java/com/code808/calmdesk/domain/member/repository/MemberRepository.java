package com.code808.calmdesk.domain.member.repository;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.company.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.code808.calmdesk.domain.company.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM MEMBER m " +
            "LEFT JOIN FETCH m.company " +
            "LEFT JOIN FETCH m.department " +
            "WHERE m.email = :email")
    Optional<Member> findEmailWithDetails(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT m FROM MEMBER m " +
            "LEFT JOIN FETCH m.company " +
            "LEFT JOIN FETCH m.department " +
            "LEFT JOIN FETCH m.rank " +
            "WHERE m.memberId = :memberId")
    Optional<Member> findByIdWithCompanyAndDepartmentAndRank(@Param("memberId") Long memberId);

    List<Member> findByDepartment(Department department);

    @Query("SELECT m FROM MEMBER m LEFT JOIN FETCH m.department LEFT JOIN FETCH m.rank WHERE m.company.companyId = :companyId AND m.status = :status")
    List<Member> findByCompany_CompanyIdAndStatusWithDetails(@Param("companyId") Long companyId, @Param("status") CommonEnums.Status status);

    @Query("SELECT m FROM MEMBER m LEFT JOIN FETCH m.department LEFT JOIN FETCH m.rank WHERE m.company.companyId = :companyId AND m.status IN :statuses ORDER BY m.createdDate DESC")
    List<Member> findByCompany_CompanyIdAndStatusInWithDetails(@Param("companyId") Long companyId, @Param("statuses") List<CommonEnums.Status> statuses);
}
