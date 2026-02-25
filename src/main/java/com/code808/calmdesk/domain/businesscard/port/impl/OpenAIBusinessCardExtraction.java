package com.code808.calmdesk.domain.businesscard.port.impl;

import com.code808.calmdesk.domain.businesscard.dto.BusinessCardExtractedDto;
import com.code808.calmdesk.domain.businesscard.port.BusinessCardExtractionPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

/**
 * 명함 이미지 → 구조화 추출 (OpenAI GPT-4o Vision).
 * app.business-card.ai.provider=openai 일 때 사용.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.business-card.ai.provider", havingValue = "openai")
public class OpenAIBusinessCardExtraction implements BusinessCardExtractionPort {

    private static final String EXTRACT_PROMPT = BusinessCardExtractionPrompt.EXTRACT_PROMPT;

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BusinessCardExtractedDto extract(byte[] imageBytes, String contentType) {
        try {
            org.springframework.util.MimeType mimeType = contentType != null && !contentType.isBlank()
                    ? MimeTypeUtils.parseMimeType(contentType)
                    : MimeTypeUtils.IMAGE_JPEG;
            Media media = Media.builder()
                    .mimeType(mimeType)
                    .data(imageBytes)
                    .build();
            UserMessage userMessage = UserMessage.builder()
                    .text(EXTRACT_PROMPT)
                    .media(List.of(media))
                    .build();
            String response = chatModel.call(new Prompt(List.of(userMessage))).getResult().getOutput().getText();
            return parseToDto(response);
        } catch (Exception e) {
            log.warn("명함 추출 실패 (OpenAI)", e);
            return BusinessCardExtractedDto.builder()
                    .extractionError("명함 인식 중 오류가 발생했습니다: " + (e.getMessage() != null ? e.getMessage() : "알 수 없음"))
                    .build();
        }
    }

    private BusinessCardExtractedDto parseToDto(String jsonText) {
        if (jsonText == null || jsonText.isBlank()) {
            return BusinessCardExtractedDto.builder().extractionError("AI가 내용을 추출하지 못했습니다.").build();
        }
        String cleaned = jsonText.trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        }
        try {
            JsonNode root = objectMapper.readTree(cleaned);
            return BusinessCardExtractedDto.builder()
                    .name(normalize("name", getText(root, "name")))
                    .company(normalize("company", getText(root, "company")))
                    .department(normalize("department", getText(root, "department")))
                    .title(normalize("title", getText(root, "title")))
                    .phone(normalize("phone", getText(root, "phone")))
                    .mobile(normalize("mobile", getText(root, "mobile")))
                    .email(normalize("email", getText(root, "email")))
                    .address(normalize("address", getText(root, "address")))
                    .fax(normalize("fax", getText(root, "fax")))
                    .website(normalize("website", getText(root, "website")))
                    .build();
        } catch (Exception e) {
            log.warn("명함 JSON 파싱 실패: {}", cleaned, e);
            return BusinessCardExtractedDto.builder()
                    .rawLines(List.of(jsonText))
                    .extractionError("추출 결과를 파싱하지 못했습니다. 원문: " + (e.getMessage() != null ? e.getMessage() : ""))
                    .build();
        }
    }

    private static String getText(JsonNode node, String key) {
        JsonNode v = node.has(key) ? node.get(key) : null;
        if (v == null || v.isNull()) return null;
        String s = v.asText();
        return (s != null && !s.isBlank()) ? s.trim() : null;
    }

    private static String normalize(String key, String value) {
        return BusinessCardValueNormalizer.normalize(key, value);
    }
}
