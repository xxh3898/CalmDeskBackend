package com.code808.calmdesk.domain.Notification.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // ⬅️ 생성일/수정일 자동 기록을 위해 필수!
@Table(name = "NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @Column(name = "NOTIFICATION_TITLE", length = 30, nullable = false)
    private String title;

    @Column(name = "NOTIFICATION_CONTENT", length = 100, nullable = false)
    private String content;

    @Builder.Default // ⬅️ 빌더 사용 시에도 기본값 "N"이 유지되도록 설정
    @Column(name = "STATUS", length = 1)
    private String status = "N";

    @CreatedDate
    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    // 읽음 상태 변경 메서드
    public void markAsRead() {
        this.status = "Y";
    }
}