package com.code808.calmdesk.domain.attendance.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "EMOTION_CHECKIN")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmotionCheckin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkinId;

    @Column(nullable = false)
    private Integer stressLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTENDANCE_ID", nullable = false)
    private Attendance attendance;

    @Column(nullable = false, length = 500)
    private String memo;

    @OneToMany(mappedBy = "emotionCheckin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StressFactor> checkinFactors = new ArrayList<>();

}
