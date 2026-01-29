package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.member.entity.Rank;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.company.repository.RankRepository;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final RankRepository rankRepository;
    private final MemberRepository memberRepository;
    private final CompanyCodeGenerator codeGenerator;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public CompanyDto.CodeResponse generateCode(){
        String code = codeGenerator.generateUniqueCode();
        return CompanyDto.CodeResponse.of(code);
    }

    @Override
    @Transactional
    public CompanyDto.RegisterResponse register(
            CompanyDto.RegisterRequest request,
            String email){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. "));

        if(member.getCompany() != null){
            throw new RuntimeException("이미 회사에 속해있습니다.");
        }

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .category(request.getCategory())
                .minValue(request.getMinValue())
                .maxValue(request.getMaxValue())
                .build();

        Company savedCompany = companyRepository.save(company);

        Department defaultDept = Department.builder()
                .departmentName("부서관리팀")
                .company(savedCompany)
                .build();

        Department savedDept = departmentRepository.save(defaultDept);

        Rank defaultRank = rankRepository.findByRankName("대표")
                .orElseThrow(() -> new RuntimeException("기본 직급을 찾을 수 없습니다"));

        member.updateCompanyInfo(
                savedCompany,
                savedDept,
                defaultRank,
                Member.Role.ADMIN,
                CommonEnums.Status.Y
        );

        String token = jwtTokenProvider.generateToken(
                member.getEmail(),
                "ADMIN"
        );

        return CompanyDto.RegisterResponse.of(savedCompany, member, token);
    }

    @Override
    public CompanyDto.CheckResponse getByCode(String CompanyCode){
        Company company = companyRepository.findByCompanyCode(CompanyCode)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 회사 코드입니다."));

        List<Department> departments = departmentRepository.findByCompany(company);
        List<Rank> ranks = rankRepository.findAll();

        return CompanyDto.CheckResponse.of(company, departments, ranks);
    }

    @Override
    @Transactional
    public CompanyDto.JoinResponse join(
            CompanyDto.JoinRequest request,
            String email){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("회원을 찾을 수 없습니다."));

        if(member.getCompany() != null){
            throw new RuntimeException("이미 회사에 속해있습니다");
        }

        Company company = companyRepository.findByCompanyCode(request.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 코드입니다"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다"));

        Rank rank = rankRepository.findById(request.getRankId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 직급입니다"));

        if (!department.getCompany().getCompanyId().equals(company.getCompanyId())) {
            throw new RuntimeException("해당 회사의 부서가 아닙니다");
        }

        member.updateCompanyInfo(
                company,
                department,
                rank,
                Member.Role.EMPLOYEE,
                CommonEnums.Status.N
        );

        String token = jwtTokenProvider.generateToken(
                member.getEmail(),
                "EMPLOYEE"
        );

        return CompanyDto.JoinResponse.of(company, member.getStatus(), token);
    }
}
