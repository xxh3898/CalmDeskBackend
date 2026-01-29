package com.code808.calmdesk.domain.company.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.dashboard.entity.DashboardWorkStatus;
import com.code808.calmdesk.domain.dashboard.repository.employee.DashboardWorkStatusRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    private final DashboardWorkStatusRepository workStatusRepository;

    @Override
    public DepartmentDto.DetailResponse getDepartmentDetails(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다. (부서 ID: " + departmentId + ")"));
        return DepartmentDto.DetailResponse.from(department);
    }

    @Override
    public List<DepartmentDto.MemberResponse> getDepartmentMembers(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다. (부서 ID: " + departmentId + ")"));

        // MemberRepository를 사용하여 해당 부서의 멤버 조회
        List<Member> members = memberRepository.findByDepartment(department);

        // 멤버들의 현재 상태 조회 (Bulk 조회)
        List<DashboardWorkStatus> statuses = workStatusRepository.findByMemberIn(members);

        // Map<MemberId, StatusDescription> 생성
        Map<Long, String> statusMap = statuses.stream()
                .collect(Collectors.toMap(
                        ws -> ws.getMember().getMemberId(),
                        ws -> ws.getStatus().getDescription()
                ));

        return members.stream()
                .map(member -> {
                    String status = statusMap.getOrDefault(member.getMemberId(), "출근 전");
                    return DepartmentDto.MemberResponse.from(member, status);
                })
                .collect(Collectors.toList());
    }
}
