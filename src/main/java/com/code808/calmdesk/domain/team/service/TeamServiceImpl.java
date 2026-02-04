package com.code808.calmdesk.domain.team.service;

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

    @Override
    public List<TeamMemberResponse> getMembersByCompanyId(Long companyId) {
        List<Member> members = memberRepository.findAllByCompanyIdWithDepartmentAndRank(companyId);
        Map<Long, Integer> remainingByMemberId = new HashMap<>();
        for (Member m : members) {
            vacationRestRepository.findByMemberId(m.getMemberId())
                    .ifPresent(vr -> remainingByMemberId.put(m.getMemberId(),
                            vr.getTotalCount() - vr.getSpentCount()));
        }

        return members.stream()
                .map(m -> TeamMemberResponse.from(m, remainingByMemberId.get(m.getMemberId())))
                .collect(Collectors.toList());
    }
}
