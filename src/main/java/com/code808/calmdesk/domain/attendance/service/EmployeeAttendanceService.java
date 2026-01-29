package com.code808.calmdesk.domain.attendance.service;

import com.code808.calmdesk.domain.attendance.dto.*;
import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.dto.VacationRequestReq;
import com.code808.calmdesk.domain.vacation.dto.VacationRequestRes;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRepository;
import com.code808.calmdesk.domain.vacation.repository.VacationRestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeAttendanceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final AttendanceRepository attendanceRepository;
    private final VacationRepository vacationRepository;
    private final VacationRestRepository vacationRestRepository;
    private final MemberRepository memberRepository;

    /**
     * 프론트 휴가 신청
     */
    @Transactional
    public VacationRequestRes requestVacation(Long memberId, VacationRequestReq req) {
        // 유효성 검사
        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new IllegalArgumentException("시작일과 종료일을 모두 입력해주세요.");
        }

        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("종료일은 시작일 이후여야 합니다.");
        }

        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // type 문자열을 Vacation.Type enum으로 변환
        Vacation.Type type = parseVacationType(req.getType());

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        int vacationDays;

        if (type == Vacation.Type.HALF) {
            // 반차: 같은 날이어야 함
            if (!req.getStartDate().equals(req.getEndDate())) {
                throw new IllegalArgumentException("반차는 시작일과 종료일이 같아야 합니다.");
            }
            // 반차: 오전/오후 선택에 따라 시간 설정
            String halfDayType = req.getHalfDayType();
            if (halfDayType != null && "오전".equals(halfDayType.trim())) {
                // 오전 반차: 09:00 ~ 13:00
                startDateTime = req.getStartDate().atTime(9, 0);
                endDateTime = req.getStartDate().atTime(13, 0);
            } else {
                // 오후 반차: 13:00 ~ 18:00 (기본값)
                startDateTime = req.getStartDate().atTime(13, 0);
                endDateTime = req.getStartDate().atTime(18, 0);
            }
            vacationDays = 1; // 반일 단위 (0.5일 = 1)
        } else if (type == Vacation.Type.WORKCATION) {
            // 워케이션: 09:00 ~ 18:00
            startDateTime = req.getStartDate().atTime(9, 0);
            endDateTime = req.getEndDate().atTime(18, 0);
            vacationDays = 0;
        } else {
            // 연차: 09:00 ~ 18:00
            startDateTime = req.getStartDate().atTime(9, 0);
            endDateTime = req.getEndDate().atTime(18, 0);
            // 시작일과 종료일 사이의 일수 계산 (포함)
            vacationDays = (int) req.getStartDate().datesUntil(req.getEndDate().plusDays(1)).count();
        }

        // reason이 null이거나 비어있으면 기본값 설정
        String reason = (req.getReason() == null || req.getReason().isBlank())
                ? "휴가 신청"
                : req.getReason();

        // Vacation 엔티티 생성 및 저장
        Vacation vacation = Vacation.builder()
                .type(type)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .reason(reason)
                .status(CommonEnums.Status.N) // 승인대기
                .vacationDays(vacationDays)
                .requestMember(member)
                .approverMember(null) // 신청 시점에는 승인자 없음
                .build();

        Vacation saved = vacationRepository.save(vacation);

        return VacationRequestRes.builder()
                .id(saved.getVacationId())
                .message("휴가 신청이 완료되었습니다.")
                .build();
    }

    /**
     * 프론트 요약 카드: 이번 달 출근 14/21일, 지각/결근 1건, 잔여 연차 12.5일, 이번 주 근무 28.5시간
     */
    @Transactional(readOnly = true)
    public AttendanceSummaryRes getSummary(Long memberId, int year, int month) {
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
        var restOpt = vacationRestRepository.findByMemberId(memberId);
        if (restOpt.isPresent()) {
            VacationRest vr = restOpt.get();
            int remain = vr.getTotalCount() - vr.getSpentCount();
            remainingVacation = remain / 2.0;
        }

        LocalDate weekStart = LocalDate.now().with(WeekFields.of(Locale.KOREAN).dayOfWeek(), 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<Attendance> weekAttendances = attendanceRepository.findByMemberAndDateRange(
                memberId, weekStart, weekEnd);
        double weekWorkHours = weekAttendances.stream()
                .filter(a -> a.getCheckIn() != null && a.getCheckOut() != null)
                .mapToDouble(a -> Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes() / 60.0)
                .sum();

        return AttendanceSummaryRes.builder()
                .monthWorkDays((int) monthWorkDays)
                .monthTotalDays(monthTotalDays)
                .lateOrAbsenceCount((int) lateOrAbsenceCount)
                .remainingVacation(remainingVacation)
                .weekWorkHours(Math.round(weekWorkHours * 10) / 10.0)
                .build();
    }

    /**
     * 프론트 전체 기록 타임라인 / 일별 상세용
     */
    @Transactional(readOnly = true)
    public List<AttendanceHistoryItemRes> getHistory(Long memberId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Attendance> list = attendanceRepository.findByMemberAndDateRange(
                memberId, start, end);

        return list.stream().map(a -> {
            String dateStr = a.getWorkDate().format(DATE_FORMAT);
            String clockInStr = a.getCheckIn() != null ? a.getCheckIn().format(TIME_FORMAT) : "-";
            String clockOutStr = a.getCheckOut() != null ? a.getCheckOut().format(TIME_FORMAT) : "-";
            String status = mapStatus(a.getAttendanceStatus());
            String duration = "-";
            if (a.getCheckIn() != null && a.getCheckOut() != null) {
                long minutes = Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes();
                long h = minutes / 60;
                long m = minutes % 60;
                duration = h + "h " + m + "m";
            }
            String note = a.getNote() != null && !a.getNote().isBlank() ? a.getNote() : "특이사항 없음";

            return AttendanceHistoryItemRes.builder()
                    .id(a.getAttendanceId())
                    .day(a.getWorkDate().getDayOfMonth())
                    .date(dateStr)
                    .clockIn(clockInStr)
                    .clockOut(clockOutStr)
                    .status(status)
                    .duration(duration)
                    .note(note)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 프론트 휴가 현황용
     */
    @Transactional(readOnly = true)
    public List<LeaveRequestItemRes> getLeaveRequests(Long memberId) {
        List<Vacation> list = vacationRepository.findByRequestMember(memberId);
        return list.stream().map(v -> {
            String type = mapVacationType(v.getType());
            String period = formatPeriod(v);
            String status = v.getStatus() == CommonEnums.Status.Y ? "승인완료" : "승인대기";
            String days = formatDays(v.getType(), v.getVacationDays());

            return LeaveRequestItemRes.builder()
                    .id(v.getVacationId())
                    .type(type)
                    .period(period)
                    .status(status)
                    .days(days)
                    .build();
        }).collect(Collectors.toList());
    }

    private String mapStatus(Attendance.AttendanceStatus s) {
        return switch (s) {
            case ATTEND -> "정상";
            case LATE -> "지각";
            case ABSENCE -> "결근";
        };
    }

    private String mapVacationType(Vacation.Type t) {
        return switch (t) {
            case ANNUAL -> "연차";
            case HALF -> "반차";
            case WORKCATION -> "워케이션";
        };
    }

    private String formatPeriod(Vacation v) {
        if (v.getType() == Vacation.Type.HALF) {
            // startDate의 시간을 기준으로 오전/오후 판단
            // 오전 반차: 09:00, 오후 반차: 13:00
            String dateStr = v.getStartDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN));
            int hour = v.getStartDate().getHour();
            String timeOfDay = hour < 13 ? "오전" : "오후"; // 13시 미만이면 오전, 13시 이상이면 오후
            return dateStr + " (" + timeOfDay + ")";
        }
        String start = v.getStartDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN));
        String end = v.getEndDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN));
        if (start.equals(end)) {
            return start;
        }
        return start + " - " + end.substring(5);
    }

    private String formatDays(Vacation.Type type, int vacationDays) {
        if (type == Vacation.Type.WORKCATION) {
            return "0.0일";
        }
        if (type == Vacation.Type.HALF) {
            return "0.5일";
        }
        return vacationDays + "일";
    }

    /**
     * 휴가 승인 (관리자용)
     * 승인 시 VacationRest의 spentCount 증가
     */
    @Transactional
    public VacationRequestRes approveVacation(Long vacationId, Long approverMemberId) {
        // 휴가 조회
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가입니다."));

        // 이미 승인된 경우
        if (vacation.getStatus() == CommonEnums.Status.Y) {
            throw new IllegalArgumentException("이미 승인된 휴가입니다.");
        }

        // 승인자 조회
        Member approver = memberRepository.findById(approverMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 승인자입니다."));

        // 휴가 승인 처리
        vacation.approve(approver);
        vacationRepository.save(vacation);

        // 워케이션이 아닌 경우에만 spentCount 증가
        if (vacation.getType() != Vacation.Type.WORKCATION) {
            var restOpt = vacationRestRepository.findByMemberId(vacation.getRequestMember().getMemberId());
            if (restOpt.isPresent()) {
                VacationRest vacationRest = restOpt.get();
                vacationRest.addSpentCount(vacation.getVacationDays());
                vacationRestRepository.save(vacationRest);
            }
        }

        return VacationRequestRes.builder()
                .id(vacation.getVacationId())
                .message("휴가가 승인되었습니다.")
                .build();
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
