package com.code808.calmdesk.domain.consultation.repository;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.code808.calmdesk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    long countByMemberAndStatus(Member member, Consultation.Status status);

    long countByStatus(Consultation.Status status);

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.member m LEFT JOIN FETCH m.department ORDER BY c.createdDate DESC")
    List<Consultation> findAllByOrderByCreatedDateDesc();

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.member m LEFT JOIN FETCH m.department WHERE m.company.companyId = :companyId ORDER BY c.createdDate DESC")
    List<Consultation> findByMember_Company_CompanyIdOrderByCreatedDateDesc(@Param("companyId") Long companyId);

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.member WHERE c.member.memberId = :memberId ORDER BY c.createdDate DESC")
    List<Consultation> findByMember_MemberIdOrderByCreatedDateDesc(@Param("memberId") Long memberId);
}
