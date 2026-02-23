package com.code808.calmdesk.domain.attendance.service;

import com.code808.calmdesk.domain.attendance.dto.StressDto;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.attendance.event.DashboardEvent;
import com.code808.calmdesk.domain.attendance.repository.StressSummaryRepository;
import com.code808.calmdesk.domain.attendance.repository.EmotionCheckinRepository;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StressSummaryServiceImpl implements StressSummaryService {

    private final StressSummaryRepository stressSummaryRepository;
    private final MemberRepository memberRepository;
    private final EmotionCheckinRepository emotionCheckinRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public StressDto.SummaryResponse createDailySummary(StressDto.SummaryRequest request) {
        Long memberId = request.getMemberId();
        LocalDate summaryDate = request.getSummaryDate();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));

        Department department = member.getDepartment();
        if (department == null) {
            throw new IllegalStateException("부서가 배정되지 않은 회원입니다: " + memberId);
        }

        Double avgStressLevel = emotionCheckinRepository.findAvgStressLevel(memberId, summaryDate);
        if (avgStressLevel == null) {
            return null;
        }

        Integer checkinCount = emotionCheckinRepository.countCheckins(memberId, summaryDate);

        Optional<StressSummary> existingSummary = stressSummaryRepository
                .findByMember_MemberIdAndSummaryDate(memberId, summaryDate);

        StressSummary savedSummary;

        if (existingSummary.isPresent()) {
            StressSummary summary = existingSummary.get();
            summary.updateStressData(avgStressLevel, checkinCount);
            savedSummary = summary;
        } else {
            StressSummary newSummary = StressSummary.builder()
                    .member(member)
                    .department(department)
                    .summaryDate(summaryDate)
                    .avgStressLevel(avgStressLevel)
                    .checkinCount(checkinCount)
                    .build();
            savedSummary = stressSummaryRepository.save(newSummary);
        }

        Long companyId = member.getCompany().getCompanyId();
        eventPublisher.publishEvent(
                new DashboardEvent(companyId)
        );

        return StressDto.SummaryResponse.builder()
                .memberId(memberId)
                .summaryDate(summaryDate)
                .avgStressLevel(savedSummary.getAvgStressLevel())
                .normalizedScore((int) Math.round(savedSummary.getAvgStressLevel() * 20))
                .checkinCount(checkinCount)
                .build();
    }
}
