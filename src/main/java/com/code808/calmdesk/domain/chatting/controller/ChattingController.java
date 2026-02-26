package com.code808.calmdesk.domain.chatting.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.chatting.dto.ChattingDto;
import com.code808.calmdesk.domain.chatting.service.ChattingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Chatting", description = "실시간 채팅 및 채팅방 관리 API")
@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chatService;

    /**
     * WebSocket 메시지 전송 클라이언트가 /pub/chat/message 로 보내면 동작
     */
    @Operation(summary = "메시지 전송 (WebSocket)", description = "WebSocket을 통해 채팅 메시지를 전송합니다. (Destination: /pub/chat/message)")
    @MessageMapping("/chat/message")
    public void sendMessage(@Parameter(description = "메시지 내용") ChattingDto.ChatMessageReq message, Principal principal) {
        // 메시지 저장
        String senderEmail = principal.getName();
        chatService.saveMessage(message, senderEmail);
    }

    /**
     * 1:1 채팅방 생성 또는 가져오기
     */
    @Operation(summary = "1:1 채팅방 생성 또는 조회", description = "상대방과의 1:1 채팅방이 있으면 가져오고, 없으면 새로 생성합니다.")
    @PostMapping("/api/chat/room")
    public ResponseEntity<String> createOrGetChatRoom(@RequestBody ChattingDto.ChatRoomCreateReq request, Principal principal) {
        String roomId = chatService.createOrGetChatRoom(
                principal.getName(), request.getTargetMemberId()
        );
        return ResponseEntity.ok(roomId);
    }

    /**
     * 내 채팅방 목록 조회
     */
    @Operation(summary = "내 채팅방 목록 조회", description = "현재 로그인한 사용자가 참여 중인 채팅방 목록을 조회합니다.")
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<ChattingDto.ChatRoomRes>> getMyChatRooms(Principal principal) {
        return ResponseEntity.ok(chatService.getMyChatRooms(principal.getName()));
    }

    /**
     * 채팅 기록 조회
     */
    @Operation(summary = "채팅 기록 조회", description = "특정 채팅방의 이전 대화 기록을 조회합니다. 페이징 처리를 지원합니다.")
    @GetMapping("/api/chat/history/{roomId}")
    public ResponseEntity<List<ChattingDto.ChatMessageRes>> getChatHistory(
            @Parameter(description = "채팅방 ID") @PathVariable("roomId") String roomId,
            @Parameter(description = "마지막으로 조회된 메시지 ID (이전 메시지 로딩 시 사용)") @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
            @Parameter(description = "조회할 메시지 개수") @RequestParam(value = "size", defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.getChatHistory(roomId, lastMessageId, size));
    }

    /**
     * 메시지 수정
     */
    @Operation(summary = "메시지 수정", description = "작성한 채팅 메시지를 수정합니다.")
    @PatchMapping("/api/chat/message/{messageId}")
    public ResponseEntity<ChattingDto.ChatMessageRes> editMessage(
            @Parameter(description = "메시지 ID") @PathVariable("messageId") Long messageId,
            @RequestBody ChattingDto.ChatMessageEditReq request,
            Principal principal) {
        return ResponseEntity.ok(chatService.editMessage(messageId, request, principal.getName()));
    }

    /**
     * 메시지 삭제
     */
    @Operation(summary = "메시지 삭제", description = "작성한 채팅 메시지를 삭제 처리합니다.")
    @DeleteMapping("/api/chat/message/{messageId}")
    public ResponseEntity<Void> deleteMessage(@Parameter(description = "메시지 ID") @PathVariable("messageId") Long messageId, Principal principal) {
        chatService.deleteMessage(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 읽음 처리
     */
    @Operation(summary = "메시지 읽음 처리", description = "채팅방의 메시지를 읽음 상태로 업데이트합니다.")
    @PostMapping("/api/chat/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "채팅방 ID") @PathVariable("roomId") String roomId,
            @RequestBody ChattingDto.ChatReadReq request,
            Principal principal) {
        chatService.markAsRead(roomId, principal.getName(), request.getLastReadMessageId());
        return ResponseEntity.ok().build();
    }

    /**
     * 회사 내 사용자 조회 (채팅 초대용)
     */
    @Operation(summary = "회사 내 사용자 조회 (초대용)", description = "채팅에 초대할 수 있는 같은 회사의 사용자 목록을 조회합니다.")
    @GetMapping("/api/chat/members")
    public ResponseEntity<List<ChattingDto.ChatMemberRes>> getCompanyMembers(Principal principal) {
        return ResponseEntity.ok(chatService.getCompanyMembers(principal.getName()));
    }

    /**
     * 채팅방 생성 (1:1 또는 그룹)
     */
    @Operation(summary = "채팅방 생성 (그룹/1:1)", description = "대상 사용자들을 포함하는 채팅방을 새로 생성합니다.")
    @PostMapping("/api/chat/create")
    public ResponseEntity<String> createChatRoom(@RequestBody ChattingDto.CreateRoomReq request, Principal principal) {
        String roomId = chatService.createChatRoom(principal.getName(), request.getTargetMemberIds());
        return ResponseEntity.ok(roomId);
    }
}
