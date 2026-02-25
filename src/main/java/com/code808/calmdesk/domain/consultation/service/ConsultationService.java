package com.code808.calmdesk.domain.consultation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.Notification.event.NotificationEvent;
import com.code808.calmdesk.domain.consultation.dto.ConsultationDto;
import com.code808.calmdesk.domain.consultation.entity.Consultation;
import com.code808.calmdesk.domain.consultation.repository.ConsultationRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createConsultation(ConsultationDto.ConsultationCreateRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. email: " + email));

        Consultation consultation = request.toEntity(member);
        Long savedId = consultationRepository.save(consultation).getCounselionId();

        // ✅ 직원 알림: 상담 신청 완료
        eventPublisher.publishEvent(new NotificationEvent(
                member.getMemberId(),
                "상담 신청 완료",
                "'" + consultation.getTitle() + "' 상담 신청이 접수되었습니다. 관리자 확인을 기다려주세요.",
                "USER",
                "/app/attendance"));

        // ✅ 관리자 알림: 같은 회사 ADMIN 전원에게 신청 사실 통보
        List<Member> admins = memberRepository.findAllByCompany_CompanyIdAndRole(
                member.getCompany().getCompanyId(), Member.Role.ADMIN);
        admins.forEach(admin -> eventPublisher.publishEvent(new NotificationEvent(
                admin.getMemberId(),
                "상담 신청 접수",
                member.getName() + "님이 상담 신청을 하였습니다.",
                "ADMIN",
                "/app/applications")));

        return savedId;
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
        Member member = memberRepository.findByEmail(email)
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

        // ✅ 직원 알림: 상담 승인(완료) 완료
        eventPublisher.publishEvent(new NotificationEvent(
                c.getMember().getMemberId(),
                "상담 신청 승인",
                "'" + c.getTitle() + "' 상담 신청이 관리자에 의해 승인되었습니다.",
                "USER",
                "/app/attendance"));
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
