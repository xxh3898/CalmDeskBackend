package com.code808.calmdesk.domain.chatting.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.code808.calmdesk.domain.chatting.entity.ChatMessage;
import com.code808.calmdesk.domain.chatting.entity.ChatRoom;
import com.code808.calmdesk.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChattingDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomCreateReq {

        @Schema(description = "대화 상대방 Member ID", example = "2")
        @JsonProperty("targetMemberId")
        private Long targetMemberId; // 대화 상대방 ID
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomRes {

        @Schema(description = "PK ID", example = "1")
        private Long id;
        @Schema(description = "채팅방 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        private String roomId;
        @Schema(description = "채팅방 이름 (그룹 채팅용)", example = "개발팀 프로젝트 회의")
        private String name;
        @Schema(description = "상대방 이름 (1:1 채팅인 경우)", example = "홍길동")
        private String targetMemberName; // 상대방 이름 (1:1 채팅인 경우)
        @Schema(description = "마지막 메시지 시간", example = "2026-02-25T15:00:00")
        private LocalDateTime lastMessageTime;
        @Schema(description = "마지막 메시지 내용", example = "네, 알겠습니다.")
        private String lastMessageContent;
        @Schema(description = "읽지 않은 메시지 수", example = "5")
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

        @Schema(description = "채팅방 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        private String roomId;
        @Schema(description = "메시지 내용", example = "안녕하세요!")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageEditReq {

        @Schema(description = "수정할 메시지 내용", example = "수정된 메시지입니다.")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatReadReq {

        @Schema(description = "마지막으로 읽은 메시지 ID", example = "150")
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

        @Schema(description = "메시지 PK ID", example = "500")
        private Long id;
        @Schema(description = "채팅방 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        private String roomId;
        @Schema(description = "발신자 이름", example = "김철수")
        private String senderName;
        @Schema(description = "발신자 Member ID", example = "1")
        private Long senderId;
        @Schema(description = "메시지 본문", example = "방가방가")
        private String content;
        @Schema(description = "메시지 생성 일시", example = "2026-02-25T15:05:00")
        private LocalDateTime createdDate;
        @Schema(description = "삭제 여부", example = "false")
        private boolean isDeleted;
        @Schema(description = "안 읽은 사람 수", example = "0")
        private int unreadCount;
        @Schema(description = "메시지 타입 (TALK, EDIT, DELETE)", example = "TALK")
        private MessageType messageType;

        public enum MessageType {
            TALK, EDIT, DELETE
        }

        public static ChatMessageRes from(ChatMessage message, MessageType type) {
            return ChatMessageRes.builder()
                    .id(message.getId())
                    .roomId(message.getChatRoom().getRoomId())
                    .senderName(message.getSender().getName())
                    .senderId(message.getSender().getMemberId())
                    .content(message.isDeleted() ? "삭제된 메시지입니다." : message.getContent())
                    .createdDate(message.getCreatedDate())
                    .isDeleted(message.isDeleted())
                    .unreadCount(0)
                    .messageType(type)
                    .build();
        }

        public static ChatMessageRes from(ChatMessage message, int unreadCount, MessageType type) {
            return ChatMessageRes.builder()
                    .id(message.getId())
                    .roomId(message.getChatRoom().getRoomId())
                    .senderName(message.getSender().getName())
                    .senderId(message.getSender().getMemberId())
                    .content(message.isDeleted() ? "삭제된 메시지입니다." : message.getContent())
                    .createdDate(message.getCreatedDate())
                    .isDeleted(message.isDeleted())
                    .unreadCount(unreadCount)
                    .messageType(type)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRoomReq {

        @Schema(description = "초대할 사용자들의 Member ID 목록", example = "[2, 3, 4]")
        private List<Long> targetMemberIds;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatMemberRes {

        @Schema(description = "사용자 ID", example = "3")
        private Long memberId;
        @Schema(description = "사용자 성함", example = "이영희")
        private String name;
        @Schema(description = "이메일 주소", example = "younghee@example.com")
        private String email;
        @Schema(description = "부서명", example = "경영지원팀")
        private String departmentName;
        @Schema(description = "직급명", example = "대리")
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
