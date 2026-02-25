package com.code808.calmdesk.domain.callrecord.port;

/**
 * 텍스트에서 욕설 횟수 판별 (AI 기반).
 */
public interface ProfanityDetectionPort {

    /**
     * 주어진 텍스트에서 욕설(비속어) 발생 횟수를 반환.
     * @param text STT 결과 등 전체 텍스트
     * @return 욕설 횟수 (0 이상)
     */
    int countProfanity(String text);
}
