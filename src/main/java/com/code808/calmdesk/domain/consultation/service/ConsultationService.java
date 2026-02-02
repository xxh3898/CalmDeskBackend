package com.code808.calmdesk.domain.consultation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.consultation.dto.ConsultationDto;
import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.code808.calmdesk.domain.consultation.repository.ConsultationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final com.code808.calmdesk.domain.member.repository.MemberRepository memberRepository;

    @Transactional
    public Long createConsultation(ConsultationDto.ConsultationCreateRequest request, String email) {
        com.code808.calmdesk.domain.member.entity.Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. email: " + email));

        Consultation consultation = request.toEntity(member);
        return consultationRepository.save(consultation).getCounselionId();
    }

    public long getWaitingCount() {
        return consultationRepository.countByStatus(Consultation.Status.WAITING);
    }
}
