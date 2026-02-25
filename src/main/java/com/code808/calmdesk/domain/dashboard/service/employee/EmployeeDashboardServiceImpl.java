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

import com.code808.calmdesk.domain.attendance.dto.StressDto;
import com.code808.calmdesk.domain.attendance.event.DashboardEvent;
import com.code808.calmdesk.domain.attendance.repository.EmotionCheckinRepository;
import com.code808.calmdesk.domain.dashboard.dto.admin.DashboardDto;
import com.code808.calmdesk.domain.dashboard.service.admin.DashboardService;
import com.code808.calmdesk.domain.dashboard.sse.SseEmitterRegistry;
import org.springframework.context.ApplicationEventPublisher;
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
import com.code808.calmdesk.domain.vacation.repository.VacationRepository;

import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.repository.employee.EmployeeDashboardRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRestRepository;
import com.code808.calmdesk.domain.attendance.service.StressSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {

    private final MemberRepository memberRepository;
    private final EmployeeDashboardRepository dashboardRepository;
    private final VacationRepository vacationRepository;
    private final AttendanceRepository attendanceRepository;
    private final com.code808.calmdesk.domain.attendance.repository.WorkStatusRepository workStatusRepository;
    private final CoolDownRepository coolDownRepository;
    private final StressSummaryService stressSummaryService;
    private final EmotionCheckinRepository emotionCheckinRepository;
    private final ApplicationEventPublisher eventPublisher;
//    private final SseEmitterRegistry sseEmitterRegistry;
//    private final DashboardService dashboardService;

    @Override
    public EmployeeDashboardResponseDto getDashboardData(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        long attendCount = dashboardRepository.countMonthlyWorkDays(memberId, startOfMonth, endOfMonth);
        long latenessCount = dashboardRepository.countMonthlyLateness(memberId, startOfMonth, endOfMonth);
        long absenceCount = dashboardRepository.countMonthlyAbsence(memberId, startOfMonth, endOfMonth);

        int totalRecordedDays = (int) (attendCount + absenceCount);
        int attendanceRate = totalRecordedDays == 0 ? 0 : (int) ((double) attendCount / totalRecordedDays * 100);

        String statusMessage;
        if (latenessCount == 0 && absenceCount == 0) {
            statusMessage = "지각/결근 없음";
        } else {
            List<String> messages = new ArrayList<>();
            if (latenessCount > 0) messages.add("지각 " + latenessCount + "회");
            if (absenceCount > 0) messages.add("결근 " + absenceCount + "회");
            statusMessage = String.join(", ", messages);
        }

        String currentStatus = "출근 전";
        LocalDateTime startTime = null;

        Optional<com.code808.calmdesk.domain.attendance.entity.WorkStatus> workStatusOpt = workStatusRepository.findByMember(member);

        if (workStatusOpt.isPresent()) {
            currentStatus = workStatusOpt.get().getStatus().getDescription();
            startTime = workStatusOpt.get().getStartTime();

            if (workStatusOpt.get().getStatus() == WorkStatusType.OFF) {
                if (startTime.toLocalDate().isBefore(today)) {
                    currentStatus = WorkStatusType.READY.getDescription();
                    startTime = null;
                }
            }
        } else {
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

        VacationRest vacationRest = vacationRepository.findByMemberId(member.getMemberId())
                .orElse(VacationRest.builder().totalCount(15).spentCount(0).build());

        int points = dashboardRepository.findCurrentPoint(memberId).orElse(0);

        Double currentStressAvg = dashboardRepository.findLatestDailyStress(member, today).orElse(null);

        int stressScore = 0;
        String stressStatus = "진단 필요";

        if (currentStressAvg != null) {
            stressScore = MonitoringDto.convertScore(currentStressAvg);
            if (stressScore <= 10) stressStatus = "매우 양호";
            else if (stressScore < 30) stressStatus = "양호";
            else if (stressScore < 70) stressStatus = "주의";
            else stressStatus = "위험";
        }

        LocalDate now = LocalDate.now();
        LocalDate thisWeekMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisWeekSunday = thisWeekMonday.plusDays(6);
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(1);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(6);

        List<Object[]> thisWeekStress = dashboardRepository.findDailyStressStats(member, thisWeekMonday, thisWeekSunday);
        List<Object[]> lastWeekStress = dashboardRepository.findDailyStressStats(member, lastWeekMonday, lastWeekSunday);

        return EmployeeDashboardResponseDto.builder()
                .userProfile(EmployeeDashboardResponseDto.UserProfile.builder().name(member.getName()).build())
                .attendanceStats(EmployeeDashboardResponseDto.AttendanceStats.builder()
                        .attendanceRate(attendanceRate)
                        .statusMessage(statusMessage)
                        .currentStatus(currentStatus)
                        .startTime(startTime)
                        .build())
                .vacationStats(EmployeeDashboardResponseDto.VacationStats.builder()
                        .totalDays(vacationRest.getTotalCount())
                        .usedDays(vacationRest.getSpentCount() / 2.0)
                        .remainingDays(vacationRest.getTotalCount() - vacationRest.getSpentCount() / 2.0)
                        .build())
                .pointStats(EmployeeDashboardResponseDto.PointStats.builder().amount(points).build())
                .stressStats(EmployeeDashboardResponseDto.StressStats.builder().score(stressScore).status(stressStatus).build())
                .weeklyStressChart(EmployeeDashboardResponseDto.WeeklyStressChart.builder()
                        .thisWeek(mapToDailyStress(thisWeekStress, thisWeekMonday))
                        .lastWeek(mapToDailyStress(lastWeekStress, lastWeekMonday))
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
        saveEmotionCheckIn(attendance, request);
        updateWorkStatus(member, WorkStatusType.WORKING);

        StressDto.SummaryRequest summaryRequest = StressDto.SummaryRequest.builder()
                .memberId(memberId)
                .summaryDate(today)
                .build();
        stressSummaryService.createDailySummary(summaryRequest);

//        pushDashboard(member.getCompany().getCompanyId());
    }

    @Override
    @Transactional
    public void clockOut(Long memberId, EmotionCheckInRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = dashboardRepository.findTodayAttendance(member, today)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다."));

        if (attendance.getCheckOut() != null) {
            throw new IllegalArgumentException("이미 퇴근하셨습니다. 하루에 한 번만 퇴근할 수 있습니다.");
        }

        attendance.setCheckOut(now);
        saveEmotionCheckIn(attendance, request);
        updateWorkStatus(member, WorkStatusType.OFF);

        StressDto.SummaryRequest summaryRequest = StressDto.SummaryRequest.builder()
                .memberId(memberId)
                .summaryDate(today)
                .build();
        stressSummaryService.createDailySummary(summaryRequest);

//        pushDashboard(member.getCompany().getCompanyId());
    }

    // 대시보드 push 공통 메서드
//    private void pushDashboard(Long companyId) {
//        try {
//            DashboardDto.DashboardRequest request = new DashboardDto.DashboardRequest();
//            request.setCompanyId(companyId);
//            DashboardDto.DashboardResponse stats = dashboardService.getAllStats(request);
//            sseEmitterRegistry.sendToCompany(companyId, "dashboard", stats);
//            log.info("대시보드 push 완료 - companyId: {}", companyId);
//        } catch (Exception e) {
//            log.error("대시보드 push 실패 - companyId: {}", companyId, e);
//        }
//    }

    private void saveEmotionCheckIn(Attendance attendance, EmotionCheckInRequest request) {
        if (request == null) return;

//        if (!attendance.getEmotionCheckins().isEmpty()) {
//            // 이미 존재한다면 첫 번째 기록을 가져와서 업데이트
//            EmotionCheckin existing = attendance.getEmotionCheckins().get(0);
//            existing.setStressLevel(request.getStressLevel());
//            existing.setMemo(request.getMemo());
//
//            // 기존 요인들 삭제 후 재생성 (또는 그대로 유지)
//            existing.getCheckinFactors().clear();
//            if (request.getStressFactors() != null) {
//                for (String factorCategory : request.getStressFactors()) {
//                    StressFactor factor = StressFactor.builder()
//                            .emotionCheckin(existing)
//                            .category(factorCategory)
//                            .build();
//                    existing.getCheckinFactors().add(factor);
//                }
//            }
//            return;
//        }

        EmotionCheckin emotionCheckin = EmotionCheckin.builder()
                .attendance(attendance)
                .stressLevel(request.getStressLevel())
                .memo(request.getMemo())
                .checkinFactors(new ArrayList<>())
                .build();

        EmotionCheckin saved = emotionCheckinRepository.save(emotionCheckin);
        addFactors(saved, request);

//        attendance.getEmotionCheckins().add(emotionCheckin);
//
//        if (request.getStressFactors() != null) {
//            for (String factorCategory : request.getStressFactors()) {
//                StressFactor factor = StressFactor.builder()
//                        .emotionCheckin(emotionCheckin)
//                        .category(factorCategory)
//                        .build();
//                emotionCheckin.getCheckinFactors().add(factor);
//            }
//        }

        //attendanceRepository.save(attendance);
    }

    private void addFactors(EmotionCheckin checkin, EmotionCheckInRequest request) {
        if (request.getStressFactors() == null) return;
        for (String factorCategory : request.getStressFactors()) {
            checkin.getCheckinFactors().add(StressFactor.builder()
                    .emotionCheckin(checkin)
                    .category(factorCategory)
                    .build());
        }
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

            eventPublisher.publishEvent(new DashboardEvent(member.getCompany().getCompanyId()));
        }
    }

    private List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> mapToDailyStress(List<Object[]> stressData, LocalDate startDate) {
        Map<LocalDate, Double> statsMap = stressData.stream()
                .collect(Collectors.toMap(
                        obj -> (LocalDate) obj[0],
                        obj -> (Double) obj[1]
                ));

        List<EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            Double score = statsMap.getOrDefault(date, 0.0);

            int normalizedScore = 0;
            if (score > 0) normalizedScore = MonitoringDto.convertScore(score);

            result.add(EmployeeDashboardResponseDto.WeeklyStressChart.DailyStress.builder()
                    .day(dayName)
                    .value(normalizedScore)
                    .build());
        }
        return result;
    }
}