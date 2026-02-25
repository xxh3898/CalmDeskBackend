package com.code808.calmdesk.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    @Schema(description = "AI의 응답 메시지", example = "오늘은 맑고 따뜻한 날씨입니다.")
    private String reply;
}
