package com.code808.calmdesk.domain.consultation.repository;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    // TODO: Member 클래스 누락으로 인해 일시적으로 제거
    // long countByMemberAndStatus(회원 회원, Consultation.Status 상태);

    long countByStatus(Consultation.Status status);
}
