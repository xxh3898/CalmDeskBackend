package com.code808.calmdesk.domain.chatting.service;

import java.util.List;

import com.code808.calmdesk.domain.chatting.dto.ChattingDto;

public interface ChattingService {

    String createOrGetChatRoom(String myEmail, Long targetMemberId);

    List<ChattingDto.ChatRoomRes> getMyChatRooms(String email);

    ChattingDto.ChatMessageRes saveMessage(ChattingDto.ChatMessageReq request, String senderEmail);

    // 채팅 기록 조회 (페이지네이션)
    List<ChattingDto.ChatMessageRes> getChatHistory(String roomId, Long lastMessageId, int size);

    ChattingDto.ChatMessageRes editMessage(Long messageId, ChattingDto.ChatMessageEditReq request, String email);

    void deleteMessage(Long messageId, String email);

    void markAsRead(String roomId, String email, Long lastReadMessageId);

    List<ChattingDto.ChatMemberRes> getCompanyMembers(String email);

    String createChatRoom(String myEmail, List<Long> targetMemberIds);
}
