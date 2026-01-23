package com.code808.calmdesk.domain.member.entity;

import com.code808.calmdesk.domain.gifticon.entity.GiftOrder;
import com.code808.calmdesk.domain.gifticon.entity.Mission_List;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    private Long id; // 멤버 아이디 (NUMBER)

    @Column(name = "NAME", length = 20)
    private String name; // 이름 (VARCHAR2(20))

    @Column(name = "EMAIL", unique = true, length = 30)
    private String email; // 이메일 (VARCHAR2(30), UNIQUE)

    @Column(name = "PHONE", unique = true, length = 30)
    private String phone; // 연락처 (VARCHAR2(30), UNIQUE)

    @Column(name = "ROLE")
    private Character role; // 권한 (CHAR)

    @Column(name = "HIRE_DATE")
    private LocalDate hireDate; // 입사일 (DATE)

    @Column(name = "PASSWORD", length = 50)
    private String password; // 비밀번호 (VARCHAR2(50))

    @Column(name = "ACTIVE")
    private Character active; // 가입 활성화 (CHAR)

    @Column(name = "ACCOUNT_ID")
    private Long accountId; // 잔여 포인트 (NUMBER)

    @Column(name = "TOTAL_EARNED")
    private Long totalEarned; // 누적 획득 포인트 (NUMBER)

    @Column(name = "TOTAL_SPENT")
    private Long totalSpent; // 누적 사용 포인트 (NUMBER)

    // 외래키들 (필요 시 연관관계 매핑 가능)
    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "RANK_ID")
    private Long rankId;

    @OneToMany(mappedBy = "member")
    private List<GiftOrder> giftOrders = new ArrayList<>();

    @OneToMany(mappedBy = "member") // 'o'를 대문자 'O'로 수정
    private List<Mission_List> missionLists = new ArrayList<>();
}