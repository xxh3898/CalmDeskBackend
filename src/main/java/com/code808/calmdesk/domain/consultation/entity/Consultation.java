package com.code808.calmdesk.domain.consultation.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consultation")
public class Consultation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counselionId;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Getter
    public enum Status {
        WAITING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @Builder
    public Consultation(String title, String description, Member member) {
        this.title = title;
        this.description = description;
        this.status = Status.WAITING;
        this.member = member;
    }

    /** 관리자 상담 처리: 상태 변경 (진행중/완료/취소) */
    public void updateStatus(Status status) {
        this.status = status;
    }
}
