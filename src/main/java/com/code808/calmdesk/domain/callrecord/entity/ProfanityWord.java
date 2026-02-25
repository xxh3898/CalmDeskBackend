package com.code808.calmdesk.domain.callrecord.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 욕설 단어 목록 (DB에 저장하여 관리)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profanity_word")
public class ProfanityWord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 욕설 단어 (예: 시발, 씨발, 바보 등) */
    @Column(name = "WORD", nullable = false, length = 50, unique = true)
    private String word;

    /** 활성화 여부 */
    @Column(name = "ACTIVE", nullable = false)
    @Builder.Default
    private Boolean active = true;
}




