package com.code808.calmdesk.domain.dashboard.repository.employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.StressSummary;
import com.code808.calmdesk.domain.member.entity.Member;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeDashboardRepository {

    private final EntityManager em;

    public Optional<Attendance> findTodayAttendance(Member member, LocalDate today) {
        List<Attendance> result = em.createQuery(
                "SELECT a FROM Attendance a WHERE a.member = :member AND a.workDate = :today", Attendance.class)
                .setParameter("member", member)
                .setParameter("today", today)
                .getResultList();
        return result.stream().findFirst();
    }

    public long countMonthlyWorkDays(Long memberId, LocalDate start, LocalDate end) {
        return em.createQuery(
                "SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId "
                + "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus IN ('ATTEND', 'LATE')", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
    }

    public long countMonthlyLateness(Long memberId, LocalDate start, LocalDate end) {
        return em.createQuery(
                "SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId "
                + "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus = 'LATE'", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
    }

    public long countMonthlyAbsence(Long memberId, LocalDate start, LocalDate end) {
        return em.createQuery(
                "SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId "
                + "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus = 'ABSENCE'", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
    }

    public Optional<StressSummary> findLatestStress(Member member) {
        List<StressSummary> result = em.createQuery(
                "SELECT s FROM STRESS_SUMMARY s WHERE s.member = :member ORDER BY s.startTime DESC", StressSummary.class)
                .setParameter("member", member)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    public Optional<Long> findCurrentPoint(Long memberId) {
        List<Long> result = em.createQuery(
                "SELECT a.remainingPoint FROM Account a WHERE a.member.memberId = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<StressSummary> findStressHistory(Member member, LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                "SELECT s FROM STRESS_SUMMARY s WHERE s.member = :member AND s.startTime BETWEEN :start AND :end ORDER BY s.startTime ASC", StressSummary.class)
                .setParameter("member", member)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
