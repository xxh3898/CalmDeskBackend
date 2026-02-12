package com.code808.calmdesk.domain.chat.entity;

import java.time.LocalDateTime;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isDeleted;

    private LocalDateTime deletedDate;

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.isDeleted = true;
        this.deletedDate = LocalDateTime.now();
    }

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, String content) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
    }
}
