package com.code808.calmdesk.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @Schema(description = "AI에게 보낼 메시지", example = "오늘 날씨 어때?")
    @NotBlank(message = "메시지를 입력하세요.")
    private String message;
}
