package com.code808.calmdesk.domain.attendance.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.company.entity.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "STRESS_SUMMARY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StressSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long summaryId;

    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(name = "avg_stress_level", nullable = false)
    private Integer avgStressLevel;

    @Column(name = "checkin_count", nullable = false)
    private Integer checkinCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}