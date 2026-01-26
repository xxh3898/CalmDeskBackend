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
    private final com.code808.calmdesk.domain.member.repository.MemberRepository memberRepository;

    @Transactional
    public Long createConsultation(ConsultationCreateRequest request) {
        // TODO: 추후 Spring Security 적용 시 실제 로그인한 사용자 정보로 교체 필요
        // 현재는 테스트를 위해 ID 1번 회원을 강제로 조회하여 할당
        com.code808.calmdesk.domain.member.entity.Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: 1"));

        Consultation consultation = request.toEntity(member);
        return consultationRepository.save(consultation).getCounselionId();
    }

    public long getWaitingCount() {
        return consultationRepository.countByStatus(Consultation.Status.WAITING);
    }
}
