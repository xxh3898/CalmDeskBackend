package com.example.demo.entity;

import com.example.demo.enums.CommonEnums;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Entity(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 30)
    private String  name;

    @Column(nullable = false, unique = true, length = 50)
    private String  email;

    @Column(nullable = false, unique = true, length = 50)
    private String  password;

    @Column(nullable = false, unique = true, length = 30)
    private String  phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;

    @Column(nullable = false)
    private Integer accountLeave;

    @Column(nullable = false)
    private Integer totalEarnedPoint;

    @Column(nullable = false)
    private Integer totalSpentPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RANK_ID", nullable = false)
    private Rank rank;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkStatus workStatus;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "requestMember", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Vacation> requestVacation = new ArrayList<>();

    @OneToMany(mappedBy = "approverMember", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Vacation> approverVacation = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<CoolDown> coolDowns = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "requestMember", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Counselion> requestCounselion = new ArrayList<>();

    @OneToMany(mappedBy = "approverMember", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Counselion> approverCounselion = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<MissionList> missionList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();


    public enum Role{
        EMPLOYEE, ADMIN
    }
}
