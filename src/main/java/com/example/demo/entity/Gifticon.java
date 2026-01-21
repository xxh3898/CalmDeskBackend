package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "GIFTICON")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Gifticon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gifticonId;

    @Column(nullable = false, length = 100)
    private String gifticonName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer period = 30;

    @OneToMany(mappedBy = "gifticon", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
