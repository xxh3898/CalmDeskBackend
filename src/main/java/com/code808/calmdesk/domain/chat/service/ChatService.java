package com.code808.calmdesk.domain.chat.service;

import com.code808.calmdesk.domain.chat.dto.ChatResponse;

public interface ChatService {

    /**
     * @param userMessage 사용자 입력
     * @param memberId 로그인한 사용자 ID (null이면 DB 컨텍스트 없이 일반 답변)
     */
    ChatResponse chat(String userMessage, Long memberId);
}
