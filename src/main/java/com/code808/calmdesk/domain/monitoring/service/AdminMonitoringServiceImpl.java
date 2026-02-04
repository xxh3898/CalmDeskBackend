package com.code808.calmdesk.domain.monitoring.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.attendance.repository.CoolDownRepository;
import com.code808.calmdesk.domain.attendance.repository.StressFactorRepository;
import com.code808.calmdesk.domain.attendance.repository.StressSummaryRepository;
import com.code808.calmdesk.domain.company.entity.Department;
import com.code808.calmdesk.domain.company.repository.DepartmentRepository;
import com.code808.calmdesk.domain.consultation.repository.ConsultationRepository;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.monitoring.dto.MonitoringDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMonitoringServiceImpl implements AdminMonitoringService {

    private final MemberRepository memberRepository;
    private final StressSummaryRepository stressSummaryRepository;
    private final ConsultationRepository consultationRepository;
    private final CoolDownRepository coolDownRepository;
    private final DepartmentRepository departmentRepository;
    private final StressFactorRepository stressFactorRepository;

    @Override
    public MonitoringDto getMonitoringData(String period) { // 기간 필터링 미구현, 현재 월/분기 로직 사용
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfMonth.atTime(LocalTime.MAX);

        // 1. 통계 (Stats)
        MonitoringDto.Stats stats = calculateStats(startOfMonth, endOfMonth, startDateTime, endDateTime);

        // 2. 추세 (Trend - 현재 포함 최근 6개월)
        List<MonitoringDto.Trend> trends = calculateTrends(now);

        // 3. 분포 (Distribution - 이번 달)
        List<MonitoringDto.Distribution> distributions = calculateDistribution(startOfMonth, endOfMonth);

        // 4. 부서별 비교 (Dept Comparison)
        List<MonitoringDto.DeptComparison> deptComparisons = calculateDeptComparison(startOfMonth, endOfMonth);

        // 5. 주요 요인 (Factors)
        List<MonitoringDto.Factor> factors = calculateFactors(startOfMonth, endOfMonth);

        return MonitoringDto.builder()
                .stats(stats)
                .trend(trends)
                .distribution(distributions)
                .deptComparison(deptComparisons)
                .factors(factors)
                .build();
    }

    private MonitoringDto.Stats calculateStats(LocalDate start, LocalDate end, LocalDateTime startDT, LocalDateTime endDT) {
        long totalMembers = memberRepository.count();

        // 평균 스트레스 (Avg Stress)
        Double avgStressVal = stressSummaryRepository.findAvgStressByDateRange(start, end);
        double currentStress = avgStressVal != null ? avgStressVal : 0.0;

        // 추세 비교를 위한 전월 데이터 (Prev Month for Trend)
        LocalDate prevStart = start.minusMonths(1);
        LocalDate prevEnd = end.minusMonths(1);
        Double prevAvrStressVal = stressSummaryRepository.findAvgStressByDateRange(prevStart, prevEnd);
        double prevStress = prevAvrStressVal != null ? prevAvrStressVal : 0.0;
        double stressDiff = currentStress - prevStress;

        // 고위험군 (High Risk)
        List<StressSummary> summaries = stressSummaryRepository.findBySummaryDateBetween(start, end);
        long highRiskCount = summaries.stream().filter(s -> s.getAvgStressLevel() >= 70).count();
        List<StressSummary> prevSummaries = stressSummaryRepository.findBySummaryDateBetween(prevStart, prevEnd);
        long prevHighRiskCount = prevSummaries.stream().filter(s -> s.getAvgStressLevel() >= 70).count();
        long riskDiff = highRiskCount - prevHighRiskCount;

        // 쿨다운 (Cooldown)
        long cooldownCount = coolDownRepository.countByCreatedDateBetween(startDT, endDT);
        double avgCooldown = totalMembers > 0 ? (double) cooldownCount / totalMembers : 0;
        long prevCooldownCount = coolDownRepository.countByCreatedDateBetween(prevStart.atStartOfDay(), prevEnd.atTime(LocalTime.MAX));
        double prevAvgCooldown = totalMembers > 0 ? (double) prevCooldownCount / totalMembers : 0;
        double cooldownDiffPercent = prevAvgCooldown > 0 ? ((avgCooldown - prevAvgCooldown) / prevAvgCooldown * 100) : 0;

        // 상담 (Consultation)
        long consultationCount = consultationRepository.countByCreatedDateBetween(startDT, endDT);
        long prevConsultationCount = consultationRepository.countByCreatedDateBetween(prevStart.atStartOfDay(), prevEnd.atTime(LocalTime.MAX));
        double consultDiffPercent = prevConsultationCount > 0 ? ((double) (consultationCount - prevConsultationCount) / prevConsultationCount * 100) : 0;

        // 직원 추세 (Employee Trend) - registerDate(가입 수락일) 기준
        long prevTotalMembers = memberRepository.countByRegisterDateBefore(start);
        long employeeDiff = totalMembers - prevTotalMembers;

        return MonitoringDto.Stats.builder()
                .totalEmployees(totalMembers + "명")
                .employeeTrend(String.format("%+d", employeeDiff))
                .avgStress(String.format("%.1f%%", currentStress))
                .stressTrend(String.format("%+.1f%%", stressDiff))
                .highRiskCount(highRiskCount + "명")
                .riskTrend(String.format("%+d", riskDiff))
                .avgCooldown(String.format("%.1f회", avgCooldown))
                .cooldownTrend(String.format("%+.0f%%", cooldownDiffPercent))
                .consultationCount(consultationCount + "건")
                .consultationTrend(String.format("%+.0f%%", consultDiffPercent))
                .build();
    }

    private List<MonitoringDto.Trend> calculateTrends(LocalDate now) {
        List<MonitoringDto.Trend> list = new ArrayList<>();
        // 최근 6개월 (Last 6 months)
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            LocalDate start = date.withDayOfMonth(1);
            LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

            Double stress = stressSummaryRepository.findAvgStressByDateRange(start, end);
            long consult = consultationRepository.countByCreatedDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));
            long cooldown = coolDownRepository.countByCreatedDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));

            list.add(MonitoringDto.Trend.builder()
                    .month(date.getMonthValue() + "월")
                    .stress(stress != null ? Math.round(stress * 10) / 10.0 : 0)
                    .consultation((int) consult)
                    .cooldown((int) cooldown)
                    .build());
        }
        return list;
    }

    private List<MonitoringDto.Distribution> calculateDistribution(LocalDate start, LocalDate end) {
        List<StressSummary> summaries = stressSummaryRepository.findBySummaryDateBetween(start, end);
        if (summaries.isEmpty()) {
            return List.of(
                    new MonitoringDto.Distribution("위험 (70%+)", 0, "#fb7185"),
                    new MonitoringDto.Distribution("주의 (40-70%)", 0, "#fca5a5"),
                    new MonitoringDto.Distribution("정상 (0-40%)", 0, "#818cf8")
            );
        }

        long risk = summaries.stream().filter(s -> s.getAvgStressLevel() >= 70).count();
        long caution = summaries.stream().filter(s -> s.getAvgStressLevel() >= 40 && s.getAvgStressLevel() < 70).count();
        long normal = summaries.stream().filter(s -> s.getAvgStressLevel() < 40).count();

        // 파이 차트를 위해 퍼센트로 변환? 프론트엔드 목업은 숫자로 되어있지만 라벨은 %임.
        // 목업 데이터: 12, 25, 63 (합 100).
        // 프론트엔드 기대에 맞춰 카운트나 퍼센트 반환. Recharts Pie는 원본 값도 잘 처리함.
        return List.of(
                new MonitoringDto.Distribution("위험 (70%+)", (int) risk, "#fb7185"),
                new MonitoringDto.Distribution("주의 (40-70%)", (int) caution, "#fca5a5"),
                new MonitoringDto.Distribution("정상 (0-40%)", (int) normal, "#818cf8")
        );
    }

    private List<MonitoringDto.DeptComparison> calculateDeptComparison(LocalDate start, LocalDate end) {
        List<Department> departments = departmentRepository.findAll();
        List<MonitoringDto.DeptComparison> list = new ArrayList<>();

        for (Department dept : departments) {
            Double avg = stressSummaryRepository.findAvgStressByDepartmentAndDateRange(dept, start, end);

            // 부서별 고위험군 수 카운트를 위해 추가 로직 필요.
            // (169~186 라인 원본 영문 주석 생략 - 아래 로직에서 전체 데이터를 가져와 필터링하는 방식으로 해결함)
            if (avg == null) {
                avg = 0.0;
            }

            // 고위험군은 아래에서 일괄 계산하여 채워넣음. 우선 0으로 초기화.
            list.add(MonitoringDto.DeptComparison.builder()
                    .dept(dept.getDepartmentName())
                    .avg(Math.round(avg * 10) / 10.0)
                    .highRisk(0)
                    .build());
        }

        // 고위험군 카운트 로직 수정 (Fix High Risk Count Logic):
        List<StressSummary> all = stressSummaryRepository.findBySummaryDateBetween(start, end);
        for (MonitoringDto.DeptComparison item : list) {
            long count = all.stream()
                    .filter(s -> s.getDepartment().getDepartmentName().equals(item.getDept()) && s.getAvgStressLevel() >= 70)
                    .count();
            item.setHighRisk((int) count);
        }

        return list;
    }

    private List<MonitoringDto.Factor> calculateFactors(LocalDate start, LocalDate end) {
        // StressFactorRepository가 LocalDateTime을 필요로 하므로 변환
        List<Object[]> results = stressFactorRepository.findTopStressFactors(start.atStartOfDay(), end.atTime(LocalTime.MAX));

        // 상위 4개 요인 (Top 4 factors)
        return results.stream().limit(4).map(obj -> {
            String factor = (String) obj[0];
            Long count = (Long) obj[1];
            // 백분율 계산. 목업 데이터는 "45" (값)이지만 차트 라벨은 "45%".
            // 전체 요인 카운트 대비 퍼센트로 계산.
            long total = results.stream().mapToLong(o -> (Long) o[1]).sum();
            int percent = total > 0 ? (int) ((count * 100) / total) : 0;
            return new MonitoringDto.Factor(factor, percent);
        }).collect(Collectors.toList());
    }
}
