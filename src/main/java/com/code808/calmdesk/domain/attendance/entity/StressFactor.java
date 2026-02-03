package com.code808.calmdesk.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STRESS_FACTOR")
public class StressFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stressFactorId;

    @Column(nullable = false, length = 30)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMOTION_CHECKIN_ID", nullable = false)
    private EmotionCheckin emotionCheckin;

}
