package com.code808.calmdesk.domain.dashboard.repository.employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
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

    public Optional<Double> findLatestDailyStress(Member member, LocalDate today) {
        List<Double> result = em.createQuery(
                "SELECT AVG(e.stressLevel) FROM EMOTION_CHECKIN e JOIN e.attendance a "
                + "WHERE a.member = :member AND a.workDate < :today "
                + "GROUP BY a.workDate "
                + "ORDER BY a.workDate DESC", Double.class)
                .setParameter("member", member)
                .setParameter("today", today)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<Object[]> findDailyStressStats(Member member, LocalDate start, LocalDate end) {
        return em.createQuery(
                "SELECT a.workDate, AVG(e.stressLevel) FROM EMOTION_CHECKIN e JOIN e.attendance a "
                + "WHERE a.member = :member AND a.workDate BETWEEN :start AND :end "
                + "GROUP BY a.workDate "
                + "ORDER BY a.workDate ASC", Object[].class)
                .setParameter("member", member)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public Optional<Integer> findCurrentPoint(Long memberId) {
        List<Integer> result = em.createQuery(
                "SELECT a.accountLeave FROM Account a WHERE a.member.memberId = :memberId", Integer.class)
                .setParameter("memberId", memberId)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }
}
