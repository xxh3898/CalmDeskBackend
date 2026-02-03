package com.code808.calmdesk.domain.attendance.entity;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "WORK_STATUS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workStatusId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatusType status;

    private java.time.LocalDateTime startTime;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID", unique = true)
    private Member member;

    public void updateStatus(WorkStatusType status, java.time.LocalDateTime startTime) {
        this.status = status;
        this.startTime = startTime;
    }
}
