package com.code808.calmdesk.domain.consultation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.code808.calmdesk.domain.member.entity.Member;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    long countByMemberAndStatus(Member member, Consultation.Status status);

    // 관리자용: 특정 회사의 상담 신청 상태별 카운트
    long countByStatusAndMember_Company_CompanyId(Consultation.Status status, Long companyId);

    // 관리자용: 특정 회사의 기간별 상담 신청 카운트
    long countByCreatedDateBetweenAndMember_Company_CompanyId(java.time.LocalDateTime start, java.time.LocalDateTime end, Long companyId);

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.member m LEFT JOIN FETCH m.department ORDER BY c.createdDate DESC")
    List<Consultation> findAllByOrderByCreatedDateDesc();

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.member m LEFT JOIN FETCH m.department WHERE m.company.companyId = :companyId ORDER BY c.createdDate DESC")
    List<Consultation> findByMember_Company_CompanyIdOrderByCreatedDateDesc(@Param("companyId") Long companyId);

    @Query("SELECT c FROM Consultation c LEFT JOIN FETCH c.member WHERE c.member.memberId = :memberId ORDER BY c.createdDate DESC")
    List<Consultation> findByMember_MemberIdOrderByCreatedDateDesc(@Param("memberId") Long memberId);
}
