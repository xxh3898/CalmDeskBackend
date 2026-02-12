package com.code808.calmdesk.domain.chat.dto;

import java.time.LocalDateTime;

import com.code808.calmdesk.domain.chat.entity.ChatMessage;
import com.code808.calmdesk.domain.chat.entity.ChatRoom;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomCreateReq {

        @JsonProperty("targetMemberId")
        private Long targetMemberId; // 대화 상대방 ID
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomRes {

        private Long id;
        private String roomId;
        private String name;
        private String targetMemberName; // 상대방 이름 (1:1 채팅인 경우)
        private LocalDateTime lastMessageTime;
        private String lastMessageContent;
        private int unreadCount;

        // Entity -> DTO 변환
        public static ChatRoomRes from(ChatRoom chatRoom, String targetMemberName, String lastMessageContent, LocalDateTime lastMessageTime, int unreadCount) {
            return ChatRoomRes.builder()
                    .id(chatRoom.getId())
                    .roomId(chatRoom.getRoomId())
                    .name(chatRoom.getName())
                    .targetMemberName(targetMemberName)
                    .lastMessageContent(lastMessageContent)
                    .lastMessageTime(lastMessageTime)
                    .unreadCount(unreadCount)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageReq {

        private String roomId;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageEditReq {

        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatReadReq {

        private Long lastReadMessageId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageRes {

        private Long id;
        private String senderName;
        private Long senderId;
        private String content;
        private LocalDateTime createdDate;
        private boolean isDeleted;
        private int unreadCount;

        public static ChatMessageRes from(ChatMessage message) {
            return ChatMessageRes.builder()
                    .id(message.getId())
                    .senderName(message.getSender().getName())
                    .senderId(message.getSender().getMemberId())
                    .content(message.isDeleted() ? "삭제된 메시지입니다." : message.getContent())
                    .createdDate(message.getCreatedDate())
                    .isDeleted(message.isDeleted())
                    .unreadCount(0) // 기본값 0, 서비스에서 별도 계산 필요 시 설정
                    .build();
        }

        public static ChatMessageRes from(ChatMessage message, int unreadCount) {
            return ChatMessageRes.builder()
                    .id(message.getId())
                    .senderName(message.getSender().getName())
                    .senderId(message.getSender().getMemberId())
                    .content(message.isDeleted() ? "삭제된 메시지입니다." : message.getContent())
                    .createdDate(message.getCreatedDate())
                    .isDeleted(message.isDeleted())
                    .unreadCount(unreadCount)
                    .build();
        }
    }
}
