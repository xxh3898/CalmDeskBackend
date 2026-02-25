package com.code808.calmdesk.domain.businesscard.port;

import com.code808.calmdesk.domain.businesscard.dto.BusinessCardExtractedDto;

/**
 * 명함 이미지 → 구조화된 텍스트 추출 (AI/OCR).
 * 구현체를 Gemini / Ollama(LLaMA) 등으로 교체 가능하게 인터페이스로 분리.
 * 설정(app.business-card.ai.provider)으로 활성 구현체만 빈 등록하면 교체는 설정 한 줄로 가능.
 */
public interface BusinessCardExtractionPort {

    /**
     * 명함 이미지 바이트에서 텍스트를 추출하고 이름/회사/전화/이메일 등으로 구조화한다.
     *
     * @param imageBytes 명함 이미지 (JPEG/PNG 등)
     * @param contentType image/jpeg, image/png 등 (일부 프로바이더에서 사용)
     * @return 구조화된 DTO. 실패 시 extractionError에 메시지 담김
     */
    BusinessCardExtractedDto extract(byte[] imageBytes, String contentType);
}
