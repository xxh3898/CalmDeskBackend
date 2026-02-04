package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;

import java.util.List;

public interface TeamService {

    List<TeamMemberResponse> getMembersByCompanyId(Long companyId);
}
