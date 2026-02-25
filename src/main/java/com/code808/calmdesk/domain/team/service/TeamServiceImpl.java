package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.attendance.repository.CoolDownRepository;
import com.code808.calmdesk.domain.attendance.repository.StressSummaryRepository;
import com.code808.calmdesk.domain.attendance.repository.WorkStatusRepository;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;
import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import com.code808.calmdesk.domain.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final MemberRepository memberRepository;
    private final StressSummaryRepository stressSummaryRepository;
    private final WorkStatusRepository workStatusRepository;
    private final CoolDownRepository coolDownRepository;
    private final AttendanceRepository attendanceRepository;
    private final VacationRepository vacationRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<TeamMemberResponse> getMembersByCompanyId(Long companyId) {
        List<Member> members = memberRepository.findAllByCompanyIdWithDepartmentAndRank(companyId);
        return buildTeamMemberResponses(members);
    }

    @Override
    public Page<TeamMemberResponse> getMembersByCompanyId(Long companyId, Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAllByCompanyIdWithDepartmentAndRankPaged(companyId, pageable);
        List<TeamMemberResponse> responses = buildTeamMemberResponses(memberPage.getContent());
        return new PageImpl<>(responses, pageable, memberPage.getTotalElements());
    }

    private List<TeamMemberResponse> buildTeamMemberResponses(List<Member> members) {
        Map<Long, Integer> remainingByMemberId = new HashMap<>();
        Map<Long, Integer> stressByMemberId = new HashMap<>();
        Map<Long, Integer> cooldownCountByMemberId = new HashMap<>();
        for (Member m : members) {
            // 연차: VacationRest 없으면 신규 직원으로 간주하여 15일, 있으면 totalCount - spentCount/2
            // (spentCount는 반차 단위)
            int remaining = vacationRepository.findByMemberId(m.getMemberId())
                    .map(vr -> (int) (vr.getTotalCount() - vr.getSpentCount() / 2.0))
                    .orElse(15);
            remainingByMemberId.put(m.getMemberId(), remaining);
            // 원시 avgStressLevel(1~5)을 0~100 점수로 환산해서 팀원 카드에 사용
            stressSummaryRepository.findTopByMember_MemberIdOrderBySummaryDateDesc(m.getMemberId())
                    .ifPresent(ss -> {
                        double raw = ss.getAvgStressLevel() != null ? ss.getAvgStressLevel() : 0.0;
                        int converted = MonitoringDto.convertScore(raw);
                        stressByMemberId.put(m.getMemberId(), converted);
                    });
            int count = (int) coolDownRepository.countByMember_MemberId(m.getMemberId());
            cooldownCountByMemberId.put(m.getMemberId(), count);
        }
        Map<Long, String> statusByMemberId = workStatusRepository.findByMemberIn(members).stream()
                .collect(Collectors.toMap(ws -> ws.getMember().getMemberId(), ws -> ws.getStatus().getDescription()));

        return members.stream()
                .map(m -> TeamMemberResponse.from(
                        m,
                        remainingByMemberId.get(m.getMemberId()),
                        stressByMemberId.get(m.getMemberId()),
                        statusByMemberId.getOrDefault(m.getMemberId(), "출근 전"),
                        cooldownCountByMemberId.getOrDefault(m.getMemberId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getMemberAttendanceByMonth(Long memberId, Long companyId, int year, int month) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        if (member.getCompany() == null || !member.getCompany().getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("해당 멤버에 대한 접근 권한이 없습니다.");
        }
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();
        LocalDateTime rangeStart = firstDay.atStartOfDay();
        LocalDateTime rangeEnd = lastDay.plusDays(1).atStartOfDay();

        Map<String, String> result = new HashMap<>();

        // 1) 휴가 먼저 채우기 (휴가/휴가예정)
        List<Vacation> vacations = vacationRepository.findByRequestMemberAndDateOverlap(memberId, rangeStart, rangeEnd);
        for (Vacation v : vacations) {
            LocalDate vStart = v.getStartDate().toLocalDate();
            LocalDate vEnd = v.getEndDate().toLocalDate();
            if (vStart.isBefore(firstDay))
                vStart = firstDay;
            if (vEnd.isAfter(lastDay))
                vEnd = lastDay;
            String label = CommonEnums.Status.Y.equals(v.getStatus()) ? "휴가" : "휴가예정";
            for (LocalDate d = vStart; !d.isAfter(vEnd); d = d.plusDays(1)) {
                result.put(String.valueOf(d.getDayOfMonth()), label);
            }
        }

        // 2) 출근/지각/결근으로 덮어쓰기
        List<Attendance> attendances = attendanceRepository.findByMemberAndDateRange(memberId, firstDay, lastDay);
        for (Attendance a : attendances) {
            int day = a.getWorkDate().getDayOfMonth();
            String statusStr = switch (a.getAttendanceStatus()) {
                case ATTEND -> "출근";
                case LATE -> "지각";
                case ABSENCE -> "결근";
            };
            result.put(String.valueOf(day), statusStr);
        }

        return result;
    }

    @Override
    public List<String> getDepartmentNamesByCompanyId(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
        return departmentRepository.findByCompany(company).stream()
                .map(Department::getDepartmentName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createDepartment(Long companyId, String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            throw new IllegalArgumentException("부서명을 입력해주세요.");
        }
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
        if (departmentRepository.findByCompanyAndDepartmentName(company, departmentName.trim()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 부서입니다.");
        }
        departmentRepository.save(Department.builder()
                .departmentName(departmentName.trim())
                .company(company)
                .build());
    }

    @Override
    public TeamStats getTeamStats(Long companyId) {
        // 전체 직원 수
        long total = memberRepository.countByCompany_CompanyId(companyId);

        // 전체 직원의 최신 StressSummary 기반으로 위험군/주의 산정
        List<Member> allMembers = memberRepository.findAllByCompanyIdWithDepartmentAndRank(companyId);

        long danger = 0;
        long caution = 0;
        for (Member m : allMembers) {
            Optional<StressSummary> ss = stressSummaryRepository
                    .findTopByMember_MemberIdOrderBySummaryDateDesc(m.getMemberId());
            if (ss.isPresent()) {
                double raw = ss.get().getAvgStressLevel() != null ? ss.get().getAvgStressLevel() : 0.0;
                int score = MonitoringDto.convertScore(raw);
                if (score >= 80)
                    danger++;
                else if (score >= 70)
                    caution++;
            }
        }
        return new TeamStats(total, danger, caution);
    }
}
