package com.code808.calmdesk.domain.chatting.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.chatting.dto.ChatDto;
import com.code808.calmdesk.domain.chatting.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * WebSocket 메시지 전송 클라이언트가 /pub/chat/message 로 보내면 동작
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto.ChatMessageReq message, Principal principal) {
        // 메시지 저장
        String senderEmail = principal.getName();
        chatService.saveMessage(message, senderEmail);
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
    public ResponseEntity<List<ChatDto.ChatMessageRes>> getChatHistory(
            @PathVariable("roomId") String roomId,
            @org.springframework.web.bind.annotation.RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
            @org.springframework.web.bind.annotation.RequestParam(value = "size", defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId, lastMessageId, size));
    }

    /**
     * 메시지 수정
     */
    @PostMapping("/api/chat/message/{messageId}/edit") // PUT method equivalent or explicit PUT
    public ResponseEntity<ChatDto.ChatMessageRes> editMessage(@PathVariable("messageId") Long messageId,
            @RequestBody ChatDto.ChatMessageEditReq request,
            Principal principal) {
        return ResponseEntity.ok(ChatDto.ChatMessageRes.from(chatService.editMessage(messageId, request, principal.getName())));
    }

    /**
     * 메시지 삭제
     */
    @PostMapping("/api/chat/message/{messageId}/delete") // Using POST for delete if needed, or DELETE
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") Long messageId, Principal principal) {
        chatService.deleteMessage(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 읽음 처리
     */
    @PostMapping("/api/chat/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("roomId") String roomId,
            @RequestBody ChatDto.ChatReadReq request,
            Principal principal) {
        chatService.markAsRead(roomId, principal.getName(), request.getLastReadMessageId());
        return ResponseEntity.ok().build();
    }

    /**
     * 회사 내 사용자 조회 (채팅 초대용)
     */
    @GetMapping("/api/chat/members")
    public ResponseEntity<List<ChatDto.ChatMemberRes>> getCompanyMembers(Principal principal) {
        return ResponseEntity.ok(chatService.getCompanyMembers(principal.getName()));
    }

    /**
     * 채팅방 생성 (1:1 또는 그룹)
     */
    @PostMapping("/api/chat/create")
    public ResponseEntity<String> createChatRoom(@RequestBody ChatDto.CreateRoomReq request, Principal principal) {
        String roomId = chatService.createChatRoom(principal.getName(), request.getTargetMemberIds());
        return ResponseEntity.ok(roomId);
    }
}
