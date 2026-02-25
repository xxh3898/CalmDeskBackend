package com.code808.calmdesk.domain.attendance.repository;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.attendance.entity.EmotionCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmotionCheckinRepository extends JpaRepository<EmotionCheckin, Long> {

    @Query("SELECT AVG(e.stressLevel) " +
            "FROM EMOTION_CHECKIN e " +
            "JOIN e.attendance a " +
            "WHERE a.member.memberId = :memberId " +
            "AND a.workDate = :workDate")
    Double findAvgStressLevel(
            @Param("memberId") Long memberId,
            @Param("workDate") LocalDate workDate
    );

    @Query("SELECT COUNT(e) FROM EMOTION_CHECKIN e " +
            "WHERE e.attendance.member.memberId = :memberId " +
            "AND e.attendance.workDate = :summaryDate")
    Integer countCheckins(
            @Param("memberId") Long memberId,
            @Param("summaryDate") LocalDate workDate
    );

    Optional<EmotionCheckin> findByAttendance(Attendance attendance);
}
