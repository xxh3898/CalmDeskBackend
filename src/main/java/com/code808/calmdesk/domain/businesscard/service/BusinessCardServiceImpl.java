package com.code808.calmdesk.domain.businesscard.service;

import com.code808.calmdesk.domain.businesscard.dto.BusinessCardExtractedDto;
import com.code808.calmdesk.domain.businesscard.dto.BusinessCardRegisterRequest;
import com.code808.calmdesk.domain.businesscard.entity.BusinessCardContact;
import com.code808.calmdesk.domain.businesscard.port.BusinessCardExtractionPort;
import com.code808.calmdesk.domain.businesscard.repository.BusinessCardContactRepository;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.company.service.CompanyService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessCardServiceImpl implements BusinessCardService {

    private final BusinessCardExtractionPort extractionPort;
    private final BusinessCardContactRepository contactRepository;
    private final MemberRepository memberRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyService companyService;

    @Override
    public BusinessCardExtractedDto extractFromImage(byte[] imageBytes, String contentType) {
        return extractionPort.extract(imageBytes, contentType);
    }

    @Override
    @Transactional
    public BusinessCardContact register(String adminEmail, BusinessCardRegisterRequest request) {
        var member = memberRepository.findEmailWithDetails(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
        Company company = member.getCompany();
        if (company == null) {
            throw new IllegalArgumentException("회사에 소속된 사용자만 명함을 등록할 수 있습니다.");
        }

        String name = request.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (request.getDepartmentId() == null) {
            throw new IllegalArgumentException("부서는 필수입니다.");
        }
        if (request.getRankId() == null) {
            throw new IllegalArgumentException("직급은 필수입니다.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));
        if (!department.getCompany().getCompanyId().equals(company.getCompanyId())) {
            throw new IllegalArgumentException("해당 회사의 부서가 아닙니다.");
        }

        String phone = normalize(request.getPhone());
        String email = normalize(request.getEmail());
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("연락처는 필수입니다.");
        }

        // 중복: 같은 회사 내 전화 또는 이메일로 기존 연락처 조회 후 업데이트
        BusinessCardContact existing = contactRepository.findByCompany_CompanyIdAndPhone(company.getCompanyId(), phone).orElse(null);
        if (existing == null) {
            existing = contactRepository.findByCompany_CompanyIdAndEmail(company.getCompanyId(), email).orElse(null);
        }

        BusinessCardContact saved;
        if (existing != null) {
            existing.setName(name);
            existing.setPhone(phone);
            existing.setEmail(email);
            existing.setDepartment(department);
            existing.setContactType(BusinessCardContact.ContactType.EMPLOYEE);
            saved = contactRepository.save(existing);
        } else {
            BusinessCardContact contact = BusinessCardContact.builder()
                    .company(company)
                    .department(department)
                    .contactType(BusinessCardContact.ContactType.EMPLOYEE)
                    .name(name)
                    .phone(phone)
                    .email(email)
                    .build();
            saved = contactRepository.save(contact);
        }

        // 항상 직원 입사 신청으로 등록 (신청관리에서 승인/반려)
        companyService.createJoinRequestFromBusinessCard(
                adminEmail,
                name,
                email,
                phone,
                request.getDepartmentId(),
                request.getRankId());

        return saved;
    }

    @Override
    public List<BusinessCardContact> listByCompany(Long companyId) {
        return contactRepository.findByCompany_CompanyIdOrderByCreatedDateDesc(companyId);
    }

    private static String normalize(String s) {
        if (s == null || s.isBlank()) return null;
        return s.trim();
    }
}
