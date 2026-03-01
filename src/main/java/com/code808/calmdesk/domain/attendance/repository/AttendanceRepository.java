package com.code808.calmdesk.domain.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT DISTINCT a FROM Attendance a JOIN FETCH a.member LEFT JOIN FETCH a.emotionCheckins "
            + "WHERE a.member.memberId = :memberId "
            + "AND a.workDate BETWEEN :start AND :end ORDER BY a.workDate DESC")
    List<Attendance> findByMemberAndDateRange(@Param("memberId") Long memberId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId "
            + "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus IN ('ATTEND', 'LATE')")
    long countWorkDaysInMonth(@Param("memberId") Long memberId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId "
            + "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus IN ('LATE', 'ABSENCE')")
    long countLateOrAbsenceInMonth(@Param("memberId") Long memberId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT MAX(a.workDate) FROM Attendance a WHERE a.member.company.companyId = :companyId")
    Optional<LocalDate> findLatestWorkDate(@Param("companyId") Long companyId);

}
