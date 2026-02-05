package com.code808.calmdesk.domain.monitoring.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public MonitoringDto getMonitoringData(String period, Integer year) {
        // 1. 기준 연도 설정
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        LocalDate now = LocalDate.now();

        LocalDate startOfPeriod;
        LocalDate endOfPeriod;
        boolean isQuarter = false;

        if ("Q1".equalsIgnoreCase(period)) {
            startOfPeriod = LocalDate.of(targetYear, 1, 1);
            endOfPeriod = LocalDate.of(targetYear, 3, 31);
            isQuarter = true;
        } else if ("Q2".equalsIgnoreCase(period)) {
            startOfPeriod = LocalDate.of(targetYear, 4, 1);
            endOfPeriod = LocalDate.of(targetYear, 6, 30);
            isQuarter = true;
        } else if ("Q3".equalsIgnoreCase(period)) {
            startOfPeriod = LocalDate.of(targetYear, 7, 1);
            endOfPeriod = LocalDate.of(targetYear, 9, 30);
            isQuarter = true;
        } else if ("Q4".equalsIgnoreCase(period)) {
            startOfPeriod = LocalDate.of(targetYear, 10, 1);
            endOfPeriod = LocalDate.of(targetYear, 12, 31);
            isQuarter = true;

        } else {
            // 월간 보기: 현재 기준 '전월' 통계 (예: 2월이면 1월 데이터 표시)
            LocalDate prevMonthDate = now.minusMonths(1);
            int prevMonth = prevMonthDate.getMonthValue();

            // targetYear가 현재 연도와 다르면, 해당 연도의 '전월' (즉, 같은 월)을 보여줄지 결정.
            // 하지만 보통 '월간'은 '최근 마감된 월'을 의미하므로, 연도가 선택되어도 그 연도의 식별된 월(prevMonth)을 사용.
            // 단, 만약 1월에 실행해서 prevMonth가 12월(작년)이 되면, targetYear 로직이 중요해짐.
            // 사용자가 명시적 연도를 선택했다면, 그 연도의 '지난 달'과 같은 월을 표시하는 것이 자연스러움.
            // 현재 로직: 선택된 연도의 (현재시간 - 1달) 월.
            // 예: 2026년 2월 5일 -> prevMonth = 1.
            // targetYear = 2026 -> 2026-01-01 ~ 2026-01-31
            // targetYear = 2025 -> 2025-01-01 ~ 2025-01-31
            startOfPeriod = LocalDate.of(targetYear, prevMonth, 1);
            endOfPeriod = startOfPeriod.withDayOfMonth(startOfPeriod.lengthOfMonth());
        }

        LocalDateTime startDateTime = startOfPeriod.atStartOfDay();
        LocalDateTime endDateTime = endOfPeriod.atTime(LocalTime.MAX);

        // 1. 통계
        MonitoringDto.Stats stats = calculateStats(startOfPeriod, endOfPeriod, startDateTime, endDateTime, isQuarter);

        // 2. 추세
        List<MonitoringDto.Trend> trends = calculateTrends(now, startOfPeriod, endOfPeriod, isQuarter);

        // 3. 분포
        List<MonitoringDto.Distribution> distributions = calculateDistribution(startOfPeriod, endOfPeriod);

        // 4. 부서별 비교
        List<MonitoringDto.DeptComparison> deptComparisons = calculateDeptComparison(startOfPeriod, endOfPeriod);

        // 5. 주요 요인
        List<MonitoringDto.Factor> factors = calculateFactors(startOfPeriod, endOfPeriod);

        return MonitoringDto.builder()
                .stats(stats)
                .trend(trends)
                .distribution(distributions)
                .deptComparison(deptComparisons)
                .factors(factors)
                .build();
    }

    private MonitoringDto.Stats calculateStats(LocalDate start, LocalDate end, LocalDateTime startDT, LocalDateTime endDT, boolean isQuarter) {
        long totalMembers = memberRepository.count();

        // 평균 스트레스
        Double avgStressVal = stressSummaryRepository.findAvgStressByDateRange(start, end);
        double currentStress = avgStressVal != null ? MonitoringDto.convertScore(avgStressVal) : 0.0;

        // 추세 비교를 위한 이전 기간 데이터
        // 분기면 3개월 전, 월간이면 1개월 전
        long minusMonths = isQuarter ? 3 : 1;
        LocalDate prevStart = start.minusMonths(minusMonths);
        LocalDate prevEnd = end.minusMonths(minusMonths);

        Double prevAvrStressVal = stressSummaryRepository.findAvgStressByDateRange(prevStart, prevEnd);
        double prevStress = prevAvrStressVal != null ? MonitoringDto.convertScore(prevAvrStressVal) : 0.0;
        double stressDiff = currentStress - prevStress;

        // 고위험군 수 (최적화: COUNT 쿼리 직접 호출)
        long highRiskCount = stressSummaryRepository.countHighRisk(start, end);
        long prevHighRiskCount = stressSummaryRepository.countHighRisk(prevStart, prevEnd);
        long riskDiff = highRiskCount - prevHighRiskCount;

        // 쿨다운 횟수
        long cooldownCount = coolDownRepository.countByCreatedDateBetween(startDT, endDT);
        double avgCooldown = totalMembers > 0 ? (double) cooldownCount / totalMembers : 0;
        long prevCooldownCount = coolDownRepository.countByCreatedDateBetween(prevStart.atStartOfDay(), prevEnd.atTime(LocalTime.MAX));
        double prevAvgCooldown = totalMembers > 0 ? (double) prevCooldownCount / totalMembers : 0;
        double cooldownDiff = avgCooldown - prevAvgCooldown;
        // 상담 신청 건수
        long consultationCount = consultationRepository.countByCreatedDateBetween(startDT, endDT);
        long prevConsultationCount = consultationRepository.countByCreatedDateBetween(prevStart.atStartOfDay(), prevEnd.atTime(LocalTime.MAX));
        double consultDiffPercent = prevConsultationCount > 0 ? ((double) (consultationCount - prevConsultationCount) / prevConsultationCount * 100) : 0;

        // 직원 수 변동 추이 (HR SaaS 가입 수락일 기준)
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
                .cooldownTrend(String.format("%+.1f회", cooldownDiff))
                .consultationCount(consultationCount + "건")
                .consultationTrend(String.format("%+.0f%%", consultDiffPercent))
                .build();
    }

    private List<MonitoringDto.Trend> calculateTrends(LocalDate now, LocalDate startOfPeriod, LocalDate endOfPeriod, boolean isQuarter) {
        List<MonitoringDto.Trend> list = new ArrayList<>();

        if (isQuarter) {
            // 분기 보기: 대상 연도의 선택된 분기 3개월을 정확히 표시
            for (int i = 0; i < 3; i++) {
                LocalDate start = startOfPeriod.plusMonths(i).withDayOfMonth(1);
                LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

                Double stress = stressSummaryRepository.findAvgStressByDateRange(start, end);
                long consult = consultationRepository.countByCreatedDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));
                long cooldown = coolDownRepository.countByCreatedDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));

                list.add(MonitoringDto.Trend.of(
                        start.getMonthValue() + "월",
                        stress,
                        (int) consult,
                        (int) cooldown
                ));
            }

        } else {
            // 월간 보기 (기본값): 오늘을 기준으로 지난 6개월간의 추세를 표시
            for (int i = 6; i >= 1; i--) {
                LocalDate date = now.minusMonths(i);
                LocalDate start = date.withDayOfMonth(1);
                LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

                Double stress = stressSummaryRepository.findAvgStressByDateRange(start, end);
                long consult = consultationRepository.countByCreatedDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));
                long cooldown = coolDownRepository.countByCreatedDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));

                list.add(MonitoringDto.Trend.of(
                        date.getMonthValue() + "월",
                        stress,
                        (int) consult,
                        (int) cooldown
                ));
            }
        }
        return list;
    }

    private List<MonitoringDto.Distribution> calculateDistribution(LocalDate start, LocalDate end) {
        // 최적화: 전체 리스트 조회 대신 COUNT 쿼리 사용
        long risk = stressSummaryRepository.countHighRisk(start, end);
        long caution = stressSummaryRepository.countCaution(start, end);
        long normal = stressSummaryRepository.countNormal(start, end);

        if (risk == 0 && caution == 0 && normal == 0) {
            return List.of(
                    new MonitoringDto.Distribution("위험 (70%+)", 0, "#fb7185"),
                    new MonitoringDto.Distribution("주의 (30-70%)", 0, "#fca5a5"),
                    new MonitoringDto.Distribution("정상 (0-30%)", 0, "#818cf8")
            );
        }

        // 파이 차트용 데이터 반환
        return List.of(
                new MonitoringDto.Distribution("위험 (70%+)", (int) risk, "#fb7185"),
                new MonitoringDto.Distribution("주의 (30-70%)", (int) caution, "#fca5a5"),
                new MonitoringDto.Distribution("정상 (0-30%)", (int) normal, "#818cf8")
        );
    }

    private List<MonitoringDto.DeptComparison> calculateDeptComparison(LocalDate start, LocalDate end) {
        // 1. 부서별 통계 데이터 한 번에 조회 (GROUP BY로 N+1 문제 해결)
        List<Object[]> results = stressSummaryRepository.findDeptStats(start, end);

        // 2. 모든 부서 목록 조회 (통계 데이터가 없는 부서도 목록에 표시하기 위함)
        List<Department> allDepts = departmentRepository.findAll();
        List<MonitoringDto.DeptComparison> list = new ArrayList<>();

        for (Department dept : allDepts) {
            // 3. 전체 부서 목록에 통계 결과 매핑
            Object[] stat = results.stream()
                    .filter(r -> r[0].equals(dept.getDepartmentName()))
                    .findFirst()
                    .orElse(null);

            Double avg = stat != null ? (Double) stat[1] : 0.0;
            Long highRiskObj = stat != null ? (Long) stat[2] : 0L;
            int highRisk = highRiskObj.intValue();

            list.add(MonitoringDto.DeptComparison.of(
                    dept.getDepartmentName(),
                    avg,
                    highRisk
            ));
        }

        return list;
    }

    private List<MonitoringDto.Factor> calculateFactors(LocalDate start, LocalDate end) {
        // StressFactorRepository는 LocalDateTime을 사용하므로 변환 필요
        List<Object[]> results = stressFactorRepository.findTopStressFactors(start.atStartOfDay(), end.atTime(LocalTime.MAX));

        // 상위 4개 요인 추출
        return results.stream().limit(4).map(obj -> {
            String factor = (String) obj[0];
            Long count = (Long) obj[1];
            // 전체 요인 대비 백분율 계산
            long total = results.stream().mapToLong(o -> (Long) o[1]).sum();
            int percent = total > 0 ? (int) ((count * 100) / total) : 0;
            return new MonitoringDto.Factor(factor, percent);
        }).collect(Collectors.toList());
    }
}
