package com.code808.calmdesk.domain.chat.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

import com.code808.calmdesk.domain.chat.dto.ChatResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private static final String QUOTA_EXCEEDED_MESSAGE =
            "Gemini API 무료 할당량을 초과했습니다. 잠시(약 1분) 후 다시 시도해 주세요. "
            + "지속되면 Google AI Studio(https://aistudio.google.com)에서 할당량·결제 설정을 확인해 주세요.";

    private final GoogleGenAiChatModel chatModel;

    @Override
    public ChatResponse chat(String userMessage) {
        try {
            // 모델이 오늘 날짜를 알 수 있도록 현재 시각(한국)을 프롬프트에 포함
            String nowKorea = ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) HH:mm"));
            String promptWithContext = "[참고: 현재 시각은 " + nowKorea + " 한국 시간입니다. 날짜·시간 관련 질문에는 이 시각을 기준으로 답변해 주세요.]\n\n" + userMessage;

            String reply = chatModel.call(promptWithContext);
            return ChatResponse.builder()
                    .reply(reply != null ? reply : "")
                    .build();
        } catch (Exception e) {
            String fullMsg = getFullMessage(e);
            if (fullMsg.contains("429") || fullMsg.contains("quota") || fullMsg.contains("Quota exceeded")) {
                log.warn("Gemini API 할당량 초과(429): {}", fullMsg);
                return ChatResponse.builder()
                        .reply(QUOTA_EXCEEDED_MESSAGE)
                        .build();
            }
            log.error("챗봇 처리 중 오류", e);
            return ChatResponse.builder()
                    .reply("챗봇 응답을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                    .build();
        }
    }

    private String getFullMessage(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (Throwable x = t; x != null; x = x.getCause()) {
            if (x.getMessage() != null) {
                if (sb.length() > 0) sb.append(" / ");
                sb.append(x.getMessage());
            }
        }
        return sb.toString();
    }
}
