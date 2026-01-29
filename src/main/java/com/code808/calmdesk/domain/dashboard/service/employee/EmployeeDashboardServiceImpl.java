package com.code808.calmdesk.domain.dashboard.service.employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.entity.DashboardWorkStatus;
import com.code808.calmdesk.domain.dashboard.entity.WorkStatus;
import com.code808.calmdesk.domain.dashboard.repository.employee.DashboardWorkStatusRepository;
import com.code808.calmdesk.domain.dashboard.repository.employee.EmployeeDashboardRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {

    private final MemberRepository memberRepository;
    private final EmployeeDashboardRepository dashboardRepository;
    private final VacationRestRepository vacationRestRepository;
    private final AttendanceRepository attendanceRepository;
    private final DashboardWorkStatusRepository workStatusRepository;

    @Override
    public EmployeeDashboardResponseDto getDashboardData(Long memberId) {
        // 1. 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 근태 통계 (이번 달)
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        long attendCount = dashboardRepository.countMonthlyWorkDays(memberId, startOfMonth, endOfMonth);
        long latenessCount = dashboardRepository.countMonthlyLateness(memberId, startOfMonth, endOfMonth);
        long absenceCount = dashboardRepository.countMonthlyAbsence(memberId, startOfMonth, endOfMonth);

        // 2-3. 출근율 계산
        int totalRecordedDays = (int) (attendCount + absenceCount);
        int attendanceRate = totalRecordedDays == 0 ? 0 : (int) ((double) attendCount / totalRecordedDays * 100);

        // 2-4. 상태 메시지 생성
        String statusMessage;
        if (latenessCount == 0 && absenceCount == 0) {
            statusMessage = "지각/결근 없음";
        } else {
            List<String> messages = new ArrayList<>();
            if (latenessCount > 0) {
                messages.add("지각 " + latenessCount + "회");
            }
            if (absenceCount > 0) {
                messages.add("결근 " + absenceCount + "회");
            }
            statusMessage = String.join(", ", messages);
        }

        // 2-5. 현재 상태 조회 (DashboardWorkStatus 우선 조회)
        String currentStatus = "출근 전";
        LocalDateTime startTime = null;

        Optional<DashboardWorkStatus> workStatusOpt = workStatusRepository.findByMember(member);

        if (workStatusOpt.isPresent()) {
            currentStatus = workStatusOpt.get().getStatus().getDescription();
            startTime = workStatusOpt.get().getStartTime();
        } else {
            // Fallback: 오늘 Attendance 기록 확인
            Optional<Attendance> todayAttendance = dashboardRepository.findTodayAttendance(member, today);
            if (todayAttendance.isPresent()) {
                Attendance a = todayAttendance.get();
                if (a.getCheckOut() != null) {
                    currentStatus = WorkStatus.OFF.getDescription();
                } else if (a.getCheckIn() != null) {
                    currentStatus = WorkStatus.WORKING.getDescription();
                    startTime = a.getCheckIn();
                }
            }
        }

        // 3. 연차 정보
        VacationRest vacationRest = vacationRestRepository.findByMemberId(member.getMemberId())
                .orElse(VacationRest.builder().totalCount(15).spentCount(0).build());

        // 4. 포인트
        int points = dashboardRepository.findCurrentPoint(memberId).orElse(0L).intValue();

        // 5. 스트레스 (최신 데이터 조회)
        StressSummary stressData = dashboardRepository.findLatestStress(member)
                .orElse(null);

        int stressScore = 0;
        String stressStatus = "진단 필요";

        if (stressData != null) {
            stressScore = stressData.getScore();
            // 점수에 따른 상태 텍스트 로직
            if (stressScore <= 30) {
                stressStatus = "매우 양호";
            } else if (stressScore <= 60) {
                stressStatus = "양호";
            } else if (stressScore <= 80) {
                stressStatus = "주의";
            } else {
                stressStatus = "위험";
            }
        }

        // 6. 주간 스트레스 데이터 (이번 주 vs 지난 주)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusDays(7);
        LocalDateTime twoWeeksAgo = now.minusDays(14);

        List<StressSummary> thisWeekStress = dashboardRepository.findStressHistory(member, oneWeekAgo, now);
        List<StressSummary> lastWeekStress = dashboardRepository.findStressHistory(member, twoWeeksAgo, oneWeekAgo);

        List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> thisWeekChartData = mapToDailyStress(thisWeekStress);
        List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> lastWeekChartData = mapToDailyStress(lastWeekStress);

        return EmployeeDashboardResponseDto.builder()
                .userProfile(EmployeeDashboardResponseDto.UserProfile.builder()
                        .name(member.getName())
                        .build())
                .attendanceStats(EmployeeDashboardResponseDto.AttendanceStats.builder()
                        .attendanceRate(attendanceRate)
                        .statusMessage(statusMessage)
                        .currentStatus(currentStatus)
                        .startTime(startTime)
                        .build())
                .vacationStats(EmployeeDashboardResponseDto.VacationStats.builder()
                        .totalDays(vacationRest.getTotalCount())
                        .usedDays((double) vacationRest.getSpentCount())
                        .remainingDays((double) (vacationRest.getTotalCount() - vacationRest.getSpentCount()))
                        .build())
                .pointStats(EmployeeDashboardResponseDto.PointStats.builder()
                        .amount(points)
                        .build())
                .stressStats(EmployeeDashboardResponseDto.StressStats.builder()
                        .score(stressScore)
                        .status(stressStatus)
                        .build())
                .weeklyStressChart(EmployeeDashboardResponseDto.WeeklyStressChart.builder()
                        .thisWeek(thisWeekChartData)
                        .lastWeek(lastWeekChartData)
                        .build())
                .build();
    }

    @Override
    @Transactional
    public void clockIn(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. Attendance 생성 (없을 경우)
        // 이미 출근한 기록이 있는지 확인?
        // 비즈니스 로직: 하루에 여러번 출근 버튼 누르면? -> 이미 출근 상태면 무시 or 에러?
        // 여기서는 "오늘 기록이 없으면 생성"으로 처리
        Optional<Attendance> todayAttendance = dashboardRepository.findTodayAttendance(member, today);
        if (todayAttendance.isEmpty()) {
            Attendance attendance = Attendance.builder()
                    .member(member)
                    .workDate(today)
                    .checkIn(now)
                    .attendanceStatus(Attendance.AttendanceStatus.ATTEND) // 기본 정상 출근으로 가정
                    .build();
            attendanceRepository.save(attendance);
        }

        // 2. DashboardWorkStatus 업데이트 -> WORKING
        updateDashboardWorkStatus(member, WorkStatus.WORKING);
    }

    @Override
    @Transactional
    public void clockOut(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. Attendance 퇴근 처리
        Attendance attendance = dashboardRepository.findTodayAttendance(member, today)
                .orElseThrow(() -> new IllegalStateException("출근 기록이 없습니다."));

        attendance.setCheckOut(now);
        // Dirty checking으로 자동 저장되지만, 명시적으로 repository save 호출 생략 가능 (Transactional)

        // 2. DashboardWorkStatus 업데이트 -> OFF
        updateDashboardWorkStatus(member, WorkStatus.OFF);
    }

    @Override
    @Transactional
    public void updateStatus(Long memberId, String statusName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        WorkStatus newStatus;
        try {
            newStatus = WorkStatus.valueOf(statusName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 상태 값입니다: " + statusName);
        }

        updateDashboardWorkStatus(member, newStatus);
    }

    private void updateDashboardWorkStatus(Member member, WorkStatus status) {
        DashboardWorkStatus workStatus = workStatusRepository.findByMember(member)
                .orElse(DashboardWorkStatus.builder()
                        .member(member)
                        .status(status)
                        .startTime(LocalDateTime.now())
                        .build());

        if (workStatus.getId() != null) {
            workStatus.updateStatus(status, LocalDateTime.now());
        } else {
            workStatusRepository.save(workStatus);
        }
    }

    private List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> mapToDailyStress(List<StressSummary> stressSummaries) {
        return stressSummaries.stream()
                .map(s -> {
                    String dayName = s.getStartTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
                    return EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress.builder()
                            .day(dayName)
                            .value(s.getScore())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
