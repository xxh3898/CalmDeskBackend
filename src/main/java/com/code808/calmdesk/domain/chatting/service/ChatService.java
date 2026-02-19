package com.code808.calmdesk.domain.chatting.service;

import java.util.List;

import com.code808.calmdesk.domain.chatting.dto.ChatDto;
import com.code808.calmdesk.domain.chatting.entity.ChatMessage;

public interface ChatService {

    String createOrGetChatRoom(String myEmail, Long targetMemberId);

    List<ChatDto.ChatRoomRes> getMyChatRooms(String email);

    ChatDto.ChatMessageRes saveMessage(ChatDto.ChatMessageReq request, String senderEmail);

    // 채팅 기록 조회 (페이지네이션)
    List<ChatDto.ChatMessageRes> getChatHistory(String roomId, Long lastMessageId, int size);

    ChatMessage editMessage(Long messageId, ChatDto.ChatMessageEditReq request, String email);

    void deleteMessage(Long messageId, String email);

    void markAsRead(String roomId, String email, Long lastReadMessageId);

    List<ChatDto.ChatMemberRes> getCompanyMembers(String email);

    String createChatRoom(String myEmail, List<Long> targetMemberIds);
}
