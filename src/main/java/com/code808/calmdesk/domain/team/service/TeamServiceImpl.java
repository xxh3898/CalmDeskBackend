package com.code808.calmdesk.domain.team.service;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.attendance.repository.CoolDownRepository;
import com.code808.calmdesk.domain.attendance.repository.StressSummaryRepository;
import com.code808.calmdesk.domain.attendance.repository.WorkStatusRepository;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.team.dto.TeamMemberResponse;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRestRepository;
import com.code808.calmdesk.domain.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final MemberRepository memberRepository;
    private final VacationRestRepository vacationRestRepository;
    private final StressSummaryRepository stressSummaryRepository;
    private final WorkStatusRepository workStatusRepository;
    private final CoolDownRepository coolDownRepository;
    private final AttendanceRepository attendanceRepository;
    private final VacationRepository vacationRepository;

    @Override
    public List<TeamMemberResponse> getMembersByCompanyId(Long companyId) {
        List<Member> members = memberRepository.findAllByCompanyIdWithDepartmentAndRank(companyId);
        Map<Long, Integer> remainingByMemberId = new HashMap<>();
        Map<Long, Integer> stressByMemberId = new HashMap<>();
        Map<Long, Integer> cooldownCountByMemberId = new HashMap<>();
        for (Member m : members) {
            vacationRestRepository.findByMemberId(m.getMemberId())
                    .ifPresent(vr -> remainingByMemberId.put(m.getMemberId(),
                            vr.getTotalCount() - vr.getSpentCount()));
            stressSummaryRepository.findLatestByMemberId(m.getMemberId())
                    .ifPresent(ss -> stressByMemberId.put(m.getMemberId(), ss.getAvgStressLevel()));
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
            if (vStart.isBefore(firstDay)) vStart = firstDay;
            if (vEnd.isAfter(lastDay)) vEnd = lastDay;
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
}
