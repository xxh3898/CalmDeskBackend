package com.code808.calmdesk.domain.company.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    private final com.code808.calmdesk.domain.attendance.repository.WorkStatusRepository workStatusRepository;

    @Override
    public DepartmentDto.DetailResponse getDepartmentDetails(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다. (부서 ID: " + departmentId + ")"));
        return DepartmentDto.DetailResponse.from(department);
    }

    @Override
    public DepartmentDto.DetailResponse getDepartmentDetailsByCompany(Long departmentId, Long companyId) {
        Department department = departmentRepository.findByDepartmentIdAndCompany_CompanyId(departmentId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없거나 접근 권한이 없습니다. (부서 ID: " + departmentId + ")"));
        return DepartmentDto.DetailResponse.from(department);
    }

    @Override
    public List<DepartmentDto.MemberResponse> getDepartmentMembers(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다. (부서 ID: " + departmentId + ")"));

        List<Member> members = memberRepository.findByDepartment(department);
        return convertToMemberResponse(members);
    }

    @Override
    public List<DepartmentDto.MemberResponse> getDepartmentMembersByCompany(Long departmentId, Long companyId) {
        Department department = departmentRepository.findByDepartmentIdAndCompany_CompanyId(departmentId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없거나 접근 권한이 없습니다. (부서 ID: " + departmentId + ")"));

        List<Member> members = memberRepository.findByDepartment(department);
        return convertToMemberResponse(members);
    }

    @Override
    public org.springframework.data.domain.Page<DepartmentDto.MemberResponse> getDepartmentMembersByCompany(Long departmentId, Long companyId, org.springframework.data.domain.Pageable pageable) {
        Department department = departmentRepository.findByDepartmentIdAndCompany_CompanyId(departmentId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없거나 접근 권한이 없습니다. (부서 ID: " + departmentId + ")"));

        org.springframework.data.domain.Page<Member> membersPage = memberRepository.findByDepartment(department, pageable);
        List<DepartmentDto.MemberResponse> content = convertToMemberResponse(membersPage.getContent());

        return new org.springframework.data.domain.PageImpl<>(content, pageable, membersPage.getTotalElements());
    }

    private List<DepartmentDto.MemberResponse> convertToMemberResponse(List<Member> members) {
        List<com.code808.calmdesk.domain.attendance.entity.WorkStatus> statuses = workStatusRepository.findByMemberIn(members);
        Map<Long, String> statusMap = statuses.stream()
                .collect(Collectors.toMap(ws -> ws.getMember().getMemberId(), ws -> ws.getStatus().getDescription()));

        return members.stream()
                .map(member -> {
                    String status = statusMap.getOrDefault(member.getMemberId(), "출근 전");
                    return DepartmentDto.MemberResponse.from(member, status);
                })
                .collect(Collectors.toList());
    }
}
