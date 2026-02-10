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

        // Entity -> DTO 변환
        public static ChatRoomRes from(ChatRoom chatRoom, String targetMemberName, String lastMessageContent, LocalDateTime lastMessageTime) {
            return ChatRoomRes.builder()
                    .id(chatRoom.getId())
                    .roomId(chatRoom.getRoomId())
                    .name(chatRoom.getName())
                    .targetMemberName(targetMemberName)
                    .lastMessageContent(lastMessageContent)
                    .lastMessageTime(lastMessageTime)
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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageRes {

        private Long id;
        private String senderName;
        private Long senderId;
        private String content;
        private LocalDateTime createdDate;

        public static ChatMessageRes from(ChatMessage message) {
            return ChatMessageRes.builder()
                    .id(message.getId())
                    .senderName(message.getSender().getName())
                    .senderId(message.getSender().getMemberId())
                    .content(message.getContent())
                    .createdDate(message.getCreatedDate())
                    .build();
        }
    }
}
