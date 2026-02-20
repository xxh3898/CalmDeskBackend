package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.member.entity.Rank;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import com.code808.calmdesk.domain.Notification.service.NotificationService;
import com.code808.calmdesk.domain.company.repository.RankRepository;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;

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
        public CompanyDto.RegisterResponse register(
                        CompanyDto.RegisterRequest request,
                        String email) {

                Member member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. "));

                if (member.getCompany() != null) {
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
                                LocalDate.now(),
                                CommonEnums.Status.Y);

                // ✨ 수정: savedCompany에서 ID를 가져와 세 번째 인자로 전달
                String token = jwtTokenProvider.generateToken(
                                member.getEmail(),
                                "ADMIN"

                );

                return CompanyDto.RegisterResponse.of(savedCompany, member, token);
        }

        @Override
        public CompanyDto.CheckResponse getByCode(String CompanyCode) {
                Company company = companyRepository.findByCompanyCode(CompanyCode)
                                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 코드입니다."));

                List<Department> departments = departmentRepository.findByCompany(company);
                List<Rank> ranks = rankRepository.findAll();

                return CompanyDto.CheckResponse.of(company, departments, ranks);
        }

        @Override
        @Transactional
        public CompanyDto.JoinResponse join(
                        CompanyDto.JoinRequest request,
                        String email) {
                Member member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

                if (member.getCompany() != null) {
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
                                LocalDate.now(),
                                CommonEnums.Status.N);

                // ✨ 수정: 조회한 company에서 ID를 가져와 세 번째 인자로 전달
                String token = jwtTokenProvider.generateToken(
                                member.getEmail(),
                                "EMPLOYEE"

                );

                // 관리자에게 알림 전송 (입사 신청 알림)
                List<Member> admins = memberRepository.findAllByCompany_CompanyIdAndRole(company.getCompanyId(),
                                Member.Role.ADMIN);

                for (Member admin : admins) {
                        Notification notification = Notification.builder()
                                        .title("입사 신청 알림")
                                        .content(member.getName() + "님이 입사를 신청했습니다.")
                                        .redirectUrl("/app/applications") // 관리자 대시보드 URL (확인 필요)
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
                if (member.getCompany() == null
                                || !member.getCompany().getCompanyId().equals(admin.getCompany().getCompanyId())) {
                        throw new RuntimeException("해당 회사의 입사 신청자가 아닙니다.");
                }
                if (member.getStatus() != CommonEnums.Status.N) {
                        throw new RuntimeException("대기 상태가 아닙니다.");
                }
                member.updateCompanyInfo(member.getCompany(), member.getDepartment(), member.getRank(),
                                member.getRole(), LocalDate.now(), CommonEnums.Status.Y);
                memberRepository.save(member);

                // 알림 생성
                Notification notification = Notification.builder()
                                .title("입사 승인 알림")
                                .content(member.getName() + "님의 입사가 승인되었습니다. 환영합니다!")
                                .redirectUrl("/app/dashboard") // 이동할 URL (대시보드 등)
                                .memberId(memberId)
                                .targetRole("USER") // 대상 역할
                                .status("N") // 읽음 여부
                                .build();

                notificationRepository.save(notification);

                // 실시간 알림 전송 (SSE)
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
                if (member.getCompany() == null
                                || !member.getCompany().getCompanyId().equals(admin.getCompany().getCompanyId())) {
                        throw new RuntimeException("해당 회사의 입사 신청자가 아닙니다.");
                }
                if (member.getStatus() != CommonEnums.Status.N) {
                        throw new RuntimeException("대기 상태가 아닙니다.");
                }
                memberRepository.delete(member);
        }

}
