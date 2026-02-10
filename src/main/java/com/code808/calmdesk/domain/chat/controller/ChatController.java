package com.code808.calmdesk.domain.chat.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.chat.dto.ChatDto;
import com.code808.calmdesk.domain.chat.entity.ChatMessage;
import com.code808.calmdesk.domain.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket 메시지 전송 클라이언트가 /pub/chat/message 로 보내면 동작
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto.ChatMessageReq message, Principal principal) {
        // 메시지 저장
        String senderEmail = principal.getName();
        ChatMessage savedMessage = chatService.saveMessage(message, senderEmail);

        // 응답 DTO 변환
        ChatDto.ChatMessageRes response = ChatDto.ChatMessageRes.from(savedMessage);

        // 구독자에게 전송 (/sub/chat/room/{roomId})
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), response);
    }

    /**
     * 1:1 채팅방 생성 또는 가져오기
     */
    @PostMapping("/api/chat/room")
    public ResponseEntity<String> createOrGetChatRoom(@RequestBody ChatDto.ChatRoomCreateReq request, Principal principal) {
        String roomId = chatService.createOrGetChatRoom(
                principal.getName(), request.getTargetMemberId()
        );
        return ResponseEntity.ok(roomId);
    }

    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<ChatDto.ChatRoomRes>> getMyChatRooms(Principal principal) {
        return ResponseEntity.ok(chatService.getMyChatRooms(principal.getName()));
    }

    /**
     * 채팅 기록 조회
     */
    @GetMapping("/api/chat/history/{roomId}")
    public ResponseEntity<List<ChatDto.ChatMessageRes>> getChatHistory(@PathVariable("roomId") String roomId) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId));
    }
}
