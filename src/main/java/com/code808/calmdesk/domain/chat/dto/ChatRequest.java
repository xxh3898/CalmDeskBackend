package com.code808.calmdesk.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "메시지를 입력하세요.")
    private String message;
}
