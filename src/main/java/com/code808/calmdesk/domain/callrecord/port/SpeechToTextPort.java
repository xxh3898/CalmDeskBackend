package com.code808.calmdesk.domain.callrecord.port;

/**
 * 음성 파일 → 텍스트 변환 (STT).
 * 구현체: Gemini 등.
 */
public interface SpeechToTextPort {

    /**
     * 녹음된 음성을 텍스트로 변환.
     * @param audioBytes 녹음 파일 (webm, wav 등)
     * @param contentType audio/webm, audio/wav 등
     * @return 변환된 텍스트. 실패 시 null 또는 예외
     */
    String transcribe(byte[] audioBytes, String contentType);
}
