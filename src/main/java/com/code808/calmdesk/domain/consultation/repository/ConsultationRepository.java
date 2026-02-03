package com.code808.calmdesk.domain.consultation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.code808.calmdesk.domain.member.entity.Member;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    long countByMemberAndStatus(Member member, Consultation.Status status);

    long countByStatus(Consultation.Status status);

    long countByCreatedDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
