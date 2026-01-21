package com.example.demo.entity;

import com.example.demo.id.CheckinFactoryId;
import com.example.demo.id.OrderId;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "CHECKIN_FACTOR")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CheckinFactory {

    @EmbeddedId
    private CheckinFactoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stressFactoryId")
    @JoinColumn(name = "STRESSFACTORY_ID")
    private StressFactory stressFactory;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("checkinId")
    @JoinColumn(name = "CHECKIN_ID")
    private EmotionCheckin checkin;
}
