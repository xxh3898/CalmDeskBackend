package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.attendance.repository.CoolDownRepository;
import com.code808.calmdesk.domain.attendance.repository.StressSummaryRepository;
import com.code808.calmdesk.domain.attendance.repository.WorkStatusRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final MemberRepository memberRepository;
    private final VacationRestRepository vacationRestRepository;
    private final StressSummaryRepository stressSummaryRepository;
    private final WorkStatusRepository workStatusRepository;
    private final CoolDownRepository coolDownRepository;

    @Override
    public List<TeamMemberResponse> getMembersByCompanyId(Long companyId) {
        List<Member> members = memberRepository.findAllByCompanyIdWithDepartmentAndRank(companyId);
        Map<Long, Integer> remainingByMemberId = new HashMap<>();
        Map<Long, Integer> stressByMemberId = new HashMap<>();
        Map<Long, Integer> cooldownCountByMemberId = new HashMap<>();
        for (Member m : members) {
            vacationRestRepository.findByMemberId(m.getMemberId())
                    .ifPresent(vr -> remainingByMemberId.put(m.getMemberId(),
                            vr.getTotalCount() - vr.getSpentCount()));
            stressSummaryRepository.findLatestByMemberId(m.getMemberId())
                    .ifPresent(ss -> stressByMemberId.put(m.getMemberId(), ss.getAvgStressLevel()));
            int count = (int) coolDownRepository.countByMember_MemberId(m.getMemberId());
            cooldownCountByMemberId.put(m.getMemberId(), count);
        }
        Map<Long, String> statusByMemberId = workStatusRepository.findByMemberIn(members).stream()
                .collect(Collectors.toMap(ws -> ws.getMember().getMemberId(), ws -> ws.getStatus().getDescription()));

        return members.stream()
                .map(m -> TeamMemberResponse.from(
                        m,
                        remainingByMemberId.get(m.getMemberId()),
                        stressByMemberId.get(m.getMemberId()),
                        statusByMemberId.getOrDefault(m.getMemberId(), "출근 전"),
                        cooldownCountByMemberId.getOrDefault(m.getMemberId(), 0)))
                .collect(Collectors.toList());
    }
}
