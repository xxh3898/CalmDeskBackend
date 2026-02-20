package com.code808.calmdesk.domain.chatting.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.code808.calmdesk.domain.chatting.entity.ChatMessage;
import com.code808.calmdesk.domain.chatting.entity.ChatRoom;
import com.code808.calmdesk.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChattingDto {

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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatReadEvent {

        private Long fromMessageId;
        private Long toMessageId;
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRoomReq {

        private List<Long> targetMemberIds;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatMemberRes {

        private Long memberId;
        private String name;
        private String email;
        private String departmentName;
        private String rankName;

        public static ChatMemberRes from(Member member) {
            return ChatMemberRes.builder()
                    .memberId(member.getMemberId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .departmentName(member.getDepartment() != null ? member.getDepartment().getDepartmentName() : "")
                    .rankName(member.getRank() != null ? member.getRank().getRankName() : "")
                    .build();
        }
    }
}
