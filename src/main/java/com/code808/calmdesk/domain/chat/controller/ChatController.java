package com.code808.calmdesk.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.chat.dto.ChatRequest;
import com.code808.calmdesk.domain.chat.dto.ChatResponse;
import com.code808.calmdesk.domain.chat.service.ChatService;
import com.code808.calmdesk.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * GET 테스트용: 브라우저/Postman에서 ?message=안녕 으로 호출
     * 예: GET http://localhost:8080/api/chat?message=안녕
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @RequestParam(value = "message", defaultValue = "Hello") String message) {
        ChatResponse response = chatService.chat(message);
        return ResponseEntity.ok(ApiResponse.success("챗봇 응답", response));
    }

    /**
     * POST: 프론트 챗봇 창에서 사용
     * Body: { "message": "사용자 입력" }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chatPost(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("챗봇 응답", response));
    }
}
