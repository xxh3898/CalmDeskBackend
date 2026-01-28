package com.code808.calmdesk.domain.attendance.repository;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a WHERE a.member.memberId = :memberId " +
           "AND a.workDate BETWEEN :start AND :end ORDER BY a.workDate DESC")
    List<Attendance> findByMemberAndDateRange(@Param("memberId") Long memberId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId " +
           "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus IN ('ATTEND', 'LATE')")
    long countWorkDaysInMonth(@Param("memberId") Long memberId,
                              @Param("start") LocalDate start,
                              @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.memberId = :memberId " +
           "AND a.workDate BETWEEN :start AND :end AND a.attendanceStatus IN ('LATE', 'ABSENCE')")
    long countLateOrAbsenceInMonth(@Param("memberId") Long memberId,
                                   @Param("start") LocalDate start,
                                   @Param("end") LocalDate end);


}
