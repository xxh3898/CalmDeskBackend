package com.example.demo.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public class CheckinFactoryId implements Serializable {

    @Column(name = "STRESSFACTORY_ID")
    private Long stressFactoryId;

    @Column(name = "CHECKIN_ID")
    private Long checkinId;
}
