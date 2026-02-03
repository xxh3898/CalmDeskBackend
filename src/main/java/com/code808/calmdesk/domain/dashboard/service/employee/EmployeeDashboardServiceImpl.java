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

import com.code808.calmdesk.domain.vacation.repository.VacationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.repository.employee.EmployeeDashboardRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {

    private final MemberRepository memberRepository;
    private final EmployeeDashboardRepository dashboardRepository;
    private final VacationRepository vacationRepository;

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

        // 2-5. 현재 상태 조회
        Optional<Attendance> todayAttendance = dashboardRepository.findTodayAttendance(member, today);
        String currentStatus = todayAttendance.map(a -> {
            if (a.getCheckOut() != null) {
                return "퇴근 완료";
            }
            if (a.getCheckIn() != null) {
                return "업무 중";
            }
            return "업무 준비 중";
        }).orElse("업무 준비 중");

        // 3. 연차 정보
        VacationRest vacationRest = vacationRepository.findByMemberId(member.getMemberId())
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
