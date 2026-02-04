package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final MemberRepository memberRepository;

    @Override
    public List<TeamMemberResponse> getMembersByCompanyId(Long companyId) {
        List<Member> members = memberRepository.findAllByCompanyIdWithDepartmentAndRank(companyId);
        return members.stream()
                .map(TeamMemberResponse::from)
                .collect(Collectors.toList());
    }
}
