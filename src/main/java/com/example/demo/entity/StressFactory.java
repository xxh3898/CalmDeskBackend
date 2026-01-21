package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STRESS_FACTORY")
public class StressFactory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stressFactoryId;

    @Column(nullable = false, length = 30)
    private String category;
}
