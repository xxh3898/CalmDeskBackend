package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TeamService {

    List<TeamMemberResponse> getMembersByCompanyId(Long companyId);

    Page<TeamMemberResponse> getMembersByCompanyId(Long companyId, Pageable pageable);

    /**
     * 특정 멤버의 해당 월 일별 근태 현황 (날짜 일 -> 출근/지각/결근/휴가/휴가예정)
     * memberId가 해당 company 소속인지 검증 후 반환
     */
    Map<String, String> getMemberAttendanceByMonth(Long memberId, Long companyId, int year, int month);

    /** 회사 소속 부서명 목록 (추가된 부서만) */
    List<String> getDepartmentNamesByCompanyId(Long companyId);

    /** 회사 소속 부서 목록 (departmentId, departmentName) - 명함 등록 팀 선택용 */
    List<DepartmentItem> getDepartmentsByCompanyId(Long companyId);

    record DepartmentItem(Long departmentId, String departmentName) {}

    /** 회사에 부서 추가 (중복 시 예외) */
    void createDepartment(Long companyId, String departmentName);
}
