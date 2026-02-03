package com.code808.calmdesk.domain.attendance.service;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final AttendanceRepository attendanceRepository;
    private final VacationRepository vacationRepository;
    private final MemberRepository memberRepository;

    /**
     * 프론트 요약 카드: 이번 달 출근 14/21일, 지각/결근 1건, 잔여 연차 12.5일, 이번 주 근무 28.5시간
     */
    @Transactional(readOnly = true)
    public AttendanceDto.AttendanceSummaryRes getSummary(Long memberId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        int monthTotalDays = (int) start.datesUntil(end.plusDays(1))
                .filter(d -> {
                    int day = d.getDayOfWeek().getValue();
                    return day >= 1 && day <= 5;
                })
                .count();

        long monthWorkDays = attendanceRepository.countWorkDaysInMonth(memberId, start, end);
        long lateOrAbsenceCount = attendanceRepository.countLateOrAbsenceInMonth(memberId, start, end);

        double remainingVacation = 0.0;
        var restOpt = vacationRepository.findByMemberId(memberId);
        if (restOpt.isPresent()) {
            VacationRest vr = restOpt.get();
            // totalCount는 일 단위로 저장됨 (예: 15일 = 15)
            // spentCount는 반차 단위로 저장됨 (연차 1일 = 2, 반차 0.5일 = 1)
            // 따라서 spentCount를 2로 나누어 일 단위로 변환
            double totalDays = vr.getTotalCount();  // 일 단위
            double spentDays = vr.getSpentCount() / 2.0;  // 반차 단위를 일 단위로 변환
            remainingVacation = totalDays - spentDays;
        }

        LocalDate weekStart = LocalDate.now().with(WeekFields.of(Locale.KOREAN).dayOfWeek(), 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<Attendance> weekAttendances = attendanceRepository.findByMemberAndDateRange(
                memberId, weekStart, weekEnd);
        double weekWorkHours = weekAttendances.stream()
                .filter(a -> a.getCheckIn() != null && a.getCheckOut() != null)
                .mapToDouble(a -> Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes() / 60.0)
                .sum();

        return AttendanceDto.AttendanceSummaryRes.of(
                (int) monthWorkDays,
                monthTotalDays,
                (int) lateOrAbsenceCount,
                remainingVacation,
                weekWorkHours);
    }

    /**
     * 프론트 전체 기록 타임라인 / 일별 상세용
     */
    @Transactional(readOnly = true)
    public List<AttendanceDto.AttendanceHistoryItemRes> getHistory(Long memberId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Attendance> list = attendanceRepository.findByMemberAndDateRange(
                memberId, start, end);

        return list.stream().map(AttendanceDto.AttendanceHistoryItemRes::of).collect(Collectors.toList());
    }

    /**
     * 프론트 휴가 현황용
     */
    @Transactional(readOnly = true)
    public List<AttendanceDto.LeaveRequestItemRes> getLeaveRequests(Long memberId) {
        List<Vacation> list = vacationRepository.findByRequestMember(memberId);
        return list.stream().map(AttendanceDto.LeaveRequestItemRes::of).collect(Collectors.toList());
    }

    /**
     * 프론트에서 받은 type 문자열을 Vacation.Type enum으로 변환
     */
    private Vacation.Type parseVacationType(String typeStr) {
        return switch (typeStr) {
            case "연차" -> Vacation.Type.ANNUAL;
            case "반차" -> Vacation.Type.HALF;
            case "워케이션" -> Vacation.Type.WORKCATION;
            default -> throw new IllegalArgumentException("유효하지 않은 휴가 종류입니다: " + typeStr);
        };
    }
}
