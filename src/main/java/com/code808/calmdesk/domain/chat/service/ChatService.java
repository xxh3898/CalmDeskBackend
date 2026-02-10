package com.code808.calmdesk.domain.chat.service;

import java.util.List;

import com.code808.calmdesk.domain.chat.dto.ChatDto;
import com.code808.calmdesk.domain.chat.entity.ChatMessage;

public interface ChatService {

    String createOrGetChatRoom(String myEmail, Long targetMemberId);

    List<ChatDto.ChatRoomRes> getMyChatRooms(String email);

    ChatMessage saveMessage(ChatDto.ChatMessageReq request, String senderEmail);

    List<ChatDto.ChatMessageRes> getChatHistory(String roomId);
}
