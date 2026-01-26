package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "GIFTICON")
@Table(name = "GIFTICON")
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

    @Column(nullable = false, length = 255)
    private String image;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer period = 30;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;

    @OneToMany(mappedBy = "gifticon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public Long getId() { return gifticonId; }
}
