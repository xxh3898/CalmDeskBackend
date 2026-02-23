package com.code808.calmdesk.domain.chat.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.chat.dto.ChatRequest;
import com.code808.calmdesk.domain.chat.dto.ChatResponse;
import com.code808.calmdesk.domain.chat.service.ChatService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;

    /**
     * POST: 프론트 챗봇 창에서 사용
     * Body: { "message": "사용자 입력" }
     * 로그인한 상태에서 호출 시 연차, 출근, 스트레스, 포인트 등 내 DB 데이터를 반영한 답변을 받습니다.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chatPost(
            @Valid @RequestBody ChatRequest request,
            Principal principal) {
        Long memberId = resolveMemberId(principal);
        ChatResponse response = chatService.chat(request.getMessage(), memberId);
        return ResponseEntity.ok(ApiResponse.success("챗봇 응답", response));
    }

    /**
     * 로그인한 사용자면 memberId, 아니면 null 반환 (채팅은 permitAll이므로 비로그인도 가능)
     */
    private Long resolveMemberId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return null;
        }
        return memberRepository.findByEmail(principal.getName())
                .map(m -> m.getMemberId())
                .orElse(null);
    }
}
