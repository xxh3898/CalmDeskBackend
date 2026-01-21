package com.example.demo.id;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Embeddable
public class OrderId implements Serializable {

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "GIFTICON_ID")
    private Long gifticonId;
}

