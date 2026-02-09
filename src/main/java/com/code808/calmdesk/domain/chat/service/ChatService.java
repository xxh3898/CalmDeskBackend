package com.code808.calmdesk.domain.chat.service;

import com.code808.calmdesk.domain.chat.dto.ChatResponse;

public interface ChatService {

    ChatResponse chat(String userMessage);
}
