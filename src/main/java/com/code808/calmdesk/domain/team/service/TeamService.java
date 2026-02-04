package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;

import java.util.List;
import java.util.Map;

public interface TeamService {

    List<TeamMemberResponse> getMembersByCompanyId(Long companyId);

    /**
     * 특정 멤버의 해당 월 일별 근태 현황 (날짜 일 -> 출근/지각/결근/휴가/휴가예정)
     * memberId가 해당 company 소속인지 검증 후 반환
     */
    Map<String, String> getMemberAttendanceByMonth(Long memberId, Long companyId, int year, int month);
}
