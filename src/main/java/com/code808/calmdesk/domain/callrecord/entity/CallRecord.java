package com.code808.calmdesk.domain.callrecord.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 통화 1건 기록. 고객 음성만 녹음 → STT → 욕설 집계 후 저장.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CALL_RECORD", indexes = {
        @Index(name = "IDX_CALL_COMPANY", columnList = "COMPANY_ID"),
        @Index(name = "IDX_CALL_EMPLOYEE", columnList = "EMPLOYEE_ID"),
        @Index(name = "IDX_CALL_PHONE", columnList = "CUSTOMER_PHONE"),
        @Index(name = "IDX_CALL_STARTED", columnList = "CALL_STARTED_AT")
})
public class CallRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

    /** 통화를 받은 직원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Member employee;

    /** 고객이 입력한 전화번호 (웹 통화 시) */
    @Column(name = "CUSTOMER_PHONE", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "CALL_STARTED_AT", nullable = false)
    private LocalDateTime callStartedAt;

    @Column(name = "CALL_ENDED_AT")
    private LocalDateTime callEndedAt;

    /** 녹음 파일 저장 경로 (서버 로컬 또는 상대 경로) */
    @Column(name = "RECORDING_PATH", length = 500)
    private String recordingPath;

    /** STT 결과 전체 텍스트 */
    @Column(name = "TRANSCRIPT", columnDefinition = "TEXT")
    private String transcript;

    /** 욕설 발생 횟수 */
    @Column(name = "PROFANITY_COUNT", nullable = false)
    private int profanityCount;

    /** 처리 상태: PENDING(녹음만), PROCESSING, DONE, FAILED */
    @Column(name = "STATUS", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @Column(name = "STATUS_MESSAGE", length = 500)
    private String statusMessage;

    public enum ProcessStatus {
        PENDING,   // 녹음 저장됨, STT/욕설 미처리
        PROCESSING,
        DONE,      // STT + 욕설 집계 완료
        FAILED
    }
}
