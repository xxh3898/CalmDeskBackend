package com.code808.calmdesk.domain.attendance.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STRESS_SUMMARY")
public class StressSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stressSummaryid;

    @Column(nullable = false, length = 30)
    private String period;

    @Column(nullable = false)
    private Integer avgStress;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

}
