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
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.global.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "AI Chat", description = "AI 챗봇과의 대화 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;

    @Operation(summary = "AI 메시지 전송", description = "AI 챗봇에게 메시지를 보내고 응답을 받습니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chatPost(
            @Valid @RequestBody ChatRequest request,
            Principal principal) {
        Member member = resolveMember(principal);
        Long memberId = member != null ? member.getMemberId() : null;
        Member.Role role = member != null ? member.getRole() : null;
        ChatResponse response = chatService.chat(request.getMessage(), memberId, role);
        return ResponseEntity.ok(ApiResponse.success("챗봇 응답", response));
    }

    private Member resolveMember(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return null;
        }
        return memberRepository.findByEmail(principal.getName()).orElse(null);
    }
}
