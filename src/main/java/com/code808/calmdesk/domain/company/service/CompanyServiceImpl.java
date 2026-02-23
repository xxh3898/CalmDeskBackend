package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import com.code808.calmdesk.domain.Notification.service.NotificationService;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.company.repository.RankRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.entity.Rank;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
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
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Override
    public CompanyDto.CodeResponse generateCode() {
        String code = codeGenerator.generateUniqueCode();
        return CompanyDto.CodeResponse.of(code);
    }

    @Override
    @Transactional
    public CompanyDto.RegisterResponse register(CompanyDto.RegisterRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        if (member.getCompany() != null) {
            throw new RuntimeException("이미 회사에 속해있습니다.");
        }

        // 회사 생성 및 저장
        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .companyCode(request.getCompanyCode())
                .category(request.getCategory())
                .minValue(request.getMinValue())
                .maxValue(request.getMaxValue())
                .build();
        Company savedCompany = companyRepository.save(company);

        // 기본 부서(부서관리팀) 생성 및 저장
        Department defaultDept = Department.builder()
                .departmentName("부서관리팀")
                .company(savedCompany)
                .build();
        Department savedDept = departmentRepository.save(defaultDept);

        // 기본 직급(대표) 조회
        Rank defaultRank = rankRepository.findByRankName("대표")
                .orElseThrow(() -> new RuntimeException("기본 직급(대표)을 찾을 수 없습니다."));

        // 회원 정보 업데이트 (관리자 권한 부여)
        member.updateCompanyInfo(
                savedCompany,
                savedDept,
                defaultRank,
                Member.Role.ADMIN,
                LocalDate.now(),
                CommonEnums.Status.Y
        );

        // 토큰 재발급
        String token = jwtTokenProvider.generateToken(
                member.getEmail(),
                "ADMIN",
                member.getMemberId(),
                member.getName(),
                savedDept.getDepartmentName()
        );

        return CompanyDto.RegisterResponse.of(savedCompany, member, token);
    }

    @Override
    public CompanyDto.CheckResponse getByCode(String companyCode) {
        Company company = companyRepository.findByCompanyCode(companyCode)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 코드입니다."));

        List<Department> departments = departmentRepository.findByCompany(company);
        List<Rank> ranks = rankRepository.findAll();

        return CompanyDto.CheckResponse.of(company, departments, ranks);
    }

    @Override
    @Transactional
    public CompanyDto.JoinResponse join(CompanyDto.JoinRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        if (member.getCompany() != null) {
            throw new RuntimeException("이미 회사에 속해있습니다.");
        }

        Company company = companyRepository.findByCompanyCode(request.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 코드입니다."));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다."));

        Rank rank = rankRepository.findById(request.getRankId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 직급입니다."));

        // 부서가 해당 회사 소속인지 검증
        if (!department.getCompany().getCompanyId().equals(company.getCompanyId())) {
            throw new RuntimeException("해당 회사의 부서가 아닙니다.");
        }

        // 회원 정보 업데이트 (승인 대기 상태 Status.N)
        member.updateCompanyInfo(
                company,
                department,
                rank,
                Member.Role.EMPLOYEE,
                LocalDate.now(),
                CommonEnums.Status.N
        );

        String token = jwtTokenProvider.generateToken(
                member.getEmail(),
                "EMPLOYEE",
                member.getMemberId(),
                member.getName(),
                department.getDepartmentName()
        );

        // 관리자에게 입사 신청 알림 전송
        List<Member> admins = memberRepository.findAllByCompany_CompanyIdAndRole(company.getCompanyId(), Member.Role.ADMIN);

        for (Member admin : admins) {
            Notification notification = Notification.builder()
                    .title("입사 신청 알림")
                    .content(member.getName() + "님이 입사를 신청했습니다.")
                    .redirectUrl("/app/applications")
                    .memberId(admin.getMemberId())
                    .targetRole("ADMIN")
                    .status("N")
                    .build();

            notificationRepository.save(notification);
            notificationService.send(admin.getMemberId(), notification);
        }

        return CompanyDto.JoinResponse.of(company, member.getStatus(), token);
    }

    @Override
    public List<CompanyDto.JoinListItemRes> listAllJoins(Long companyId) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        return memberRepository.findByCompany_CompanyIdAndStatusInWithDetails(
                        companyId,
                        Arrays.asList(CommonEnums.Status.N, CommonEnums.Status.Y, CommonEnums.Status.R))
                .stream()
                .map(CompanyDto.JoinListItemRes::of)
                .toList();
    }

    @Override
    @Transactional
    public void approveJoin(Long memberId, String adminEmail) {
        Member admin = memberRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("승인자를 찾을 수 없습니다."));

        if (admin.getCompany() == null) {
            throw new RuntimeException("관리자는 회사에 소속되어 있어야 합니다.");
        }

        Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
                .orElseThrow(() -> new RuntimeException("대상을 찾을 수 없습니다."));

        if (member.getCompany() == null || !member.getCompany().getCompanyId().equals(admin.getCompany().getCompanyId())) {
            throw new RuntimeException("해당 회사의 입사 신청자가 아닙니다.");
        }

        if (member.getStatus() != CommonEnums.Status.N) {
            throw new RuntimeException("대기 상태가 아닙니다.");
        }

        // 상태 승인(Y)으로 변경
        member.updateCompanyInfo(member.getCompany(), member.getDepartment(), member.getRank(),
                member.getRole(), LocalDate.now(), CommonEnums.Status.Y);

        // 알림 생성 및 SSE 전송
        Notification notification = Notification.builder()
                .title("입사 승인 알림")
                .content(member.getName() + "님의 입사가 승인되었습니다. 환영합니다!")
                .redirectUrl("/app/dashboard")
                .memberId(memberId)
                .targetRole("USER")
                .status("N")
                .build();

        notificationRepository.save(notification);
        notificationService.send(memberId, notification);
    }

    @Override
    @Transactional
    public void rejectJoin(Long memberId, String adminEmail) {
        Member admin = memberRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("승인자를 찾을 수 없습니다."));

        if (admin.getCompany() == null) {
            throw new RuntimeException("관리자는 회사에 소속되어 있어야 합니다.");
        }

        Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
                .orElseThrow(() -> new RuntimeException("대상을 찾을 수 없습니다."));

        if (member.getCompany() == null || !member.getCompany().getCompanyId().equals(admin.getCompany().getCompanyId())) {
            throw new RuntimeException("해당 회사의 입사 신청자가 아닙니다.");
        }

        if (member.getStatus() != CommonEnums.Status.N) {
            throw new RuntimeException("대기 상태가 아닙니다.");
        }

        // 거절 시 데이터 처리 (요구사항에 따라 delete 혹은 Status.R 변경)
        memberRepository.delete(member);
    }
}