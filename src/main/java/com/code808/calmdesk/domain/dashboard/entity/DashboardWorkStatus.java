package com.code808.calmdesk.domain.dashboard.entity;

import java.time.LocalDateTime;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dashboard_work_status")
public class DashboardWorkStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status;

    private LocalDateTime startTime;

    @Builder
    public DashboardWorkStatus(Member member, WorkStatus status, LocalDateTime startTime) {
        this.member = member;
        this.status = status;
        this.startTime = startTime;
    }

    public void updateStatus(WorkStatus status, LocalDateTime startTime) {
        this.status = status;
        this.startTime = startTime;
    }
}
