package com.code808.calmdesk.domain.consultation.service;

import com.code808.calmdesk.domain.consultation.dto.ConsultationCreateRequest;
import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.code808.calmdesk.domain.consultation.repository.ConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;

    @Transactional
    public Long createConsultation(ConsultationCreateRequest request) {
        // TODO: 로그인된 Member 정보 가져와서 연관관계 설정 필요
        Consultation consultation = request.toEntity();
        return consultationRepository.save(consultation).getCounselionId();
    }

    public long getWaitingCount() {
        // TODO: 특정 Member의 대기 건수만 조회하도록 수정 필요
        return consultationRepository.countByStatus(Consultation.Status.WAITING);
    }
}
