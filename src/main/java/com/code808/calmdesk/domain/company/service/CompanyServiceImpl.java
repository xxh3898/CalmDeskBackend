package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.company.service.CompanyCodeGenerator;
import com.code808.calmdesk.domain.member.entity.Company;
import com.code808.calmdesk.domain.member.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.common.enums.CommonEnums;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    private final CompanyCodeGenerator codeGenerator;

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
                .departmentName("전체")
                .company(savedCompany)
                .build();

        Department savedDept = departmentRepository.save(defaultDept);

        member.updateCompnayInfo(
                savedCompany,
                savedDept,
                Member.Role.ADMIN,
                CommonEnums.Status.Y
        );

        return CompanyDto.RegisterResponse.of(savedCompany, member);
    }
}
