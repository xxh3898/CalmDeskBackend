package com.code808.calmdesk.domain.consultation.service;

import java.util.List;
import java.util.stream.Collectors;

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

    public long getWaitingCount(Long companyId) {
        return consultationRepository.countByStatusAndMember_Company_CompanyId(Consultation.Status.WAITING, companyId);
    }

    /**
     * 관리자: 회사별 상담 목록 (전체 상태 유지, 휴가/입사 탭과 동일)
     */
    public List<ConsultationDto.ConsultationListItemRes> getConsultationListByCompany(Long companyId) {
        return consultationRepository.findByMember_Company_CompanyIdOrderByCreatedDateDesc(companyId).stream()
                .map(ConsultationDto.ConsultationListItemRes::from)
                .collect(Collectors.toList());
    }

    /**
     * 직원: 본인 상담 신청 목록 (근태 캘린더용)
     */
    public List<ConsultationDto.ConsultationListItemRes> getMyConsultationList(String email) {
        com.code808.calmdesk.domain.member.entity.Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return consultationRepository.findByMember_MemberIdOrderByCreatedDateDesc(member.getMemberId()).stream()
                .map(ConsultationDto.ConsultationListItemRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void completeConsultation(Long consultationId) {
        Consultation c = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 신청입니다."));
        if (c.getStatus() == Consultation.Status.COMPLETED) {
            throw new IllegalArgumentException("이미 완료된 상담입니다.");
        }
        if (c.getStatus() == Consultation.Status.CANCELLED) {
            throw new IllegalArgumentException("취소된 상담은 완료할 수 없습니다.");
        }
        c.updateStatus(Consultation.Status.COMPLETED);
        consultationRepository.save(c);
    }

    @Transactional
    public void cancelConsultation(Long consultationId) {
        Consultation c = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 신청입니다."));
        if (c.getStatus() == Consultation.Status.CANCELLED) {
            throw new IllegalArgumentException("이미 취소된 상담입니다.");
        }
        if (c.getStatus() == Consultation.Status.COMPLETED) {
            throw new IllegalArgumentException("완료된 상담은 취소할 수 없습니다.");
        }
        c.updateStatus(Consultation.Status.CANCELLED);
        consultationRepository.save(c);
    }
}
