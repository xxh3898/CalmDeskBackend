package com.code808.calmdesk.domain.dashboard.service.employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.CoolDown;
import com.code808.calmdesk.domain.attendance.entity.EmotionCheckin;
import com.code808.calmdesk.domain.attendance.entity.StressFactor;
import com.code808.calmdesk.domain.attendance.entity.WorkStatusType;
import com.code808.calmdesk.domain.attendance.repository.AttendanceRepository;
import com.code808.calmdesk.domain.attendance.repository.CoolDownRepository;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmotionCheckInRequest;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.repository.employee.EmployeeDashboardRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;
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
    private final com.code808.calmdesk.domain.attendance.repository.WorkStatusRepository workStatusRepository;
    private final CoolDownRepository coolDownRepository;

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

        // 2-5. 현재 상태 조회 (WorkStatus 우선 조회)
        String currentStatus = "출근 전";
        LocalDateTime startTime = null;

        Optional<com.code808.calmdesk.domain.attendance.entity.WorkStatus> workStatusOpt = workStatusRepository.findByMember(member);

        if (workStatusOpt.isPresent()) {
            currentStatus = workStatusOpt.get().getStatus().getDescription();
            startTime = workStatusOpt.get().getStartTime();

            // 만약 상태가 '퇴근'이고, 시작 시간이 오늘 이전이라면 '출근 전'으로 표시
            if (workStatusOpt.get().getStatus() == WorkStatusType.OFF) {
                if (startTime.toLocalDate().isBefore(today)) {
                    currentStatus = WorkStatusType.READY.getDescription();
                    startTime = null;
                }
            }
        } else {
            // Fallback: 오늘 Attendance 기록 확인
            Optional<Attendance> todayAttendance = dashboardRepository.findTodayAttendance(member, today);
            if (todayAttendance.isPresent()) {
                Attendance a = todayAttendance.get();
                if (a.getCheckOut() != null) {
                    currentStatus = WorkStatusType.OFF.getDescription();
                } else if (a.getCheckIn() != null) {
                    currentStatus = WorkStatusType.WORKING.getDescription();
                    startTime = a.getCheckIn();
                }
            }
        }

        // 3. 연차 정보
        VacationRest vacationRest = vacationRestRepository.findByMemberId(member.getMemberId())
                .orElse(VacationRest.builder().totalCount(15).spentCount(0).build());

        // 4. 포인트
        int points = dashboardRepository.findCurrentPoint(memberId).orElse(0);

        // 5. 스트레스 (최신 데이터 조회) - 데이터가 존재하는 가장 최근 '과거' 날짜의 평균
        Double currentStressAvg = dashboardRepository.findLatestDailyStress(member, today)
                .orElse(null);

        int stressScore = 0;
        String stressStatus = "진단 필요";

        if (currentStressAvg != null) {
            stressScore = MonitoringDto.convertScore(currentStressAvg); // 1~5 -> 0~100 (비선형 변환)
            // 점수에 따른 상태 텍스트 로직
            if (stressScore <= 10) {
                stressStatus = "매우 양호";
            } else if (stressScore < 30) {
                stressStatus = "양호";
            } else if (stressScore < 70) {
                stressStatus = "주의";
            } else {
                stressStatus = "위험";
            }
        }

        // 6. 주간 스트레스 데이터 (이번 주 vs 지난 주) - 월요일 기준 고정
        LocalDate now = LocalDate.now();
        LocalDate thisWeekMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisWeekSunday = thisWeekMonday.plusDays(6);

        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(1);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(6);

        List<Object[]> thisWeekStress = dashboardRepository.findDailyStressStats(member, thisWeekMonday, thisWeekSunday);
        List<Object[]> lastWeekStress = dashboardRepository.findDailyStressStats(member, lastWeekMonday, lastWeekSunday);

        List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> thisWeekChartData = mapToDailyStress(thisWeekStress, thisWeekMonday);
        List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> lastWeekChartData = mapToDailyStress(lastWeekStress, lastWeekMonday);

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
    public void clockIn(Long memberId, EmotionCheckInRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. Attendance 생성 (없을 경우) or 조회
        if (dashboardRepository.findTodayAttendance(member, today).isPresent()) {
            throw new IllegalArgumentException("이미 출근하셨습니다. 하루에 한 번만 출근할 수 있습니다.");
        }

        Attendance newAttendance = Attendance.builder()
                .member(member)
                .workDate(today)
                .checkIn(now)
                .attendanceStatus(Attendance.AttendanceStatus.ATTEND)
                .emotionCheckins(new ArrayList<>())
                .build();
        Attendance attendance = attendanceRepository.save(newAttendance);

        // 2. 감정 체크인 저장
        saveEmotionCheckIn(attendance, request);

        // 3. WorkStatus 업데이트 -> WORKING
        updateWorkStatus(member, WorkStatusType.WORKING);
    }

    @Override
    @Transactional
    public void clockOut(Long memberId, EmotionCheckInRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. Attendance 퇴근 처리
        Attendance attendance = dashboardRepository.findTodayAttendance(member, today)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다."));

        if (attendance.getCheckOut() != null) {
            throw new IllegalArgumentException("이미 퇴근하셨습니다. 하루에 한 번만 퇴근할 수 있습니다.");
        }

        attendance.setCheckOut(now);

        // 2. 감정 체크인 저장 (퇴근 시 기분)
        saveEmotionCheckIn(attendance, request);

        // 3. WorkStatus 업데이트 -> OFF
        updateWorkStatus(member, WorkStatusType.OFF);
    }

    private void saveEmotionCheckIn(Attendance attendance, EmotionCheckInRequest request) {
        if (request == null) {
            return;
        }

        EmotionCheckin emotionCheckin = EmotionCheckin.builder()
                .attendance(attendance)
                .stressLevel(request.getStressLevel())
                .memo(request.getMemo())
                .checkinFactors(new ArrayList<>())
                .build();

        // 양방향 연관관계 메서드 혹은 직접 리스트에 추가
        attendance.getEmotionCheckins().add(emotionCheckin);

        // StressFactor 저장
        if (request.getStressFactors() != null) {
            for (String factorCategory : request.getStressFactors()) {
                StressFactor factor = StressFactor.builder()
                        .emotionCheckin(emotionCheckin)
                        .category(factorCategory)
                        .build();
                emotionCheckin.getCheckinFactors().add(factor);
            }
        }

        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void updateStatus(Long memberId, String statusName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        WorkStatusType newStatus;
        try {
            newStatus = WorkStatusType.valueOf(statusName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 상태 값입니다: " + statusName);
        }

        updateWorkStatus(member, newStatus);
    }

    private void updateWorkStatus(Member member, WorkStatusType status) {
        com.code808.calmdesk.domain.attendance.entity.WorkStatus workStatus = workStatusRepository.findByMember(member)
                .orElse(com.code808.calmdesk.domain.attendance.entity.WorkStatus.builder()
                        .member(member)
                        .status(status)
                        .startTime(LocalDateTime.now())
                        .build());

        if (workStatus.getWorkStatusId() != null) {
            workStatus.updateStatus(status, LocalDateTime.now());
        } else {
            workStatusRepository.save(workStatus);
        }

        if (status == WorkStatusType.COOLDOWN) {
            coolDownRepository.save(new CoolDown(null, member));
        }
    }

    private List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> mapToDailyStress(List<Object[]> stressData, LocalDate startDate) {
        // 1. DB 결과를 Map으로 변환
        Map<LocalDate, Double> statsMap = stressData.stream()
                .collect(Collectors.toMap(
                        obj -> (LocalDate) obj[0],
                        obj -> (Double) obj[1]
                ));

        List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> result = new ArrayList<>();

        // 2. 시작일(월요일)부터 7일간 순회하며 데이터 채우기 (없으면 0)
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            Double score = statsMap.getOrDefault(date, 0.0);

            // 1~5점 척도 -> 0~100점 만점 환산 (비선형 변환)
            // 데이터가 없어서 0.0인 경우, convertScore(0) -> 0 처리
            int normalizedScore = 0;
            if (score > 0) {
                normalizedScore = MonitoringDto.convertScore(score);
            }

            result.add(EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress.builder()
                    .day(dayName)
                    .value(normalizedScore)
                    .build());
        }
        return result;
    }
}
