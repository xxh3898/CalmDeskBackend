package com.code808.calmdesk.domain.company.service;

import java.util.List;
import java.util.stream.Collectors;

import com.code808.calmdesk.domain.company.entity.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;
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

        return members.stream()
                .map(DepartmentDto.MemberResponse::from)
                .collect(Collectors.toList());
    }
}
