package com.code808.calmdesk.domain.chat.service;

import com.code808.calmdesk.domain.chat.dto.ChatResponse;
import com.code808.calmdesk.domain.member.entity.Member;

public interface ChatService {

    /**
     * @param userMessage 사용자 입력
     * @param memberId    로그인한 사용자 ID (null이면 DB 컨텍스트 없이 일반 답변)
     * @param role        로그인한 사용자 권한 (ADMIN / EMPLOYEE, null이면 직원 가이드 적용)
     */
    ChatResponse chat(String userMessage, Long memberId, Member.Role role);
}
