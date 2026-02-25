package com.code808.calmdesk.domain.callrecord.port.impl;

import com.code808.calmdesk.domain.callrecord.entity.ProfanityWord;
import com.code808.calmdesk.domain.callrecord.port.ProfanityDetectionPort;
import com.code808.calmdesk.domain.callrecord.repository.ProfanityWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 로컬 필터링으로 텍스트 내 욕설(비속어) 횟수 판별.
 * DB에 저장된 욕설 목록과 비교하여 카운트.
 * app.call-record.profanity.provider=local 시 사용.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.call-record.profanity.provider", havingValue = "local", matchIfMissing = false)
public class LocalProfanityDetection implements ProfanityDetectionPort {

    private final ProfanityWordRepository profanityWordRepository;
    
    // 캐시된 욕설 목록 (성능 최적화)
    private List<String> cachedProfanityWords = null;
    private long lastCacheUpdate = 0;
    private static final long CACHE_TTL_MS = 60000; // 1분 캐시

    @Override
    public int countProfanity(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        try {
            // DB에서 활성화된 욕설 단어 목록 가져오기 (캐시 사용)
            List<String> profanityWords = getProfanityWords();
            
            if (profanityWords.isEmpty()) {
                log.warn("욕설 단어 목록이 비어있습니다. DB에 욕설 단어를 등록해주세요.");
                return 0;
            }

            int count = 0;
            String lowerText = text.toLowerCase(); // 대소문자 구분 없이 비교

            // 각 욕설 단어가 텍스트에 포함되어 있는지 확인
            for (String word : profanityWords) {
                String lowerWord = word.toLowerCase();
                
                // 정확히 일치하거나 부분 일치하는 경우 카운트
                // 예: "시발"이 "시발놈"에 포함되거나 "시발" 자체로 존재
                Pattern pattern = Pattern.compile(Pattern.quote(lowerWord), Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(lowerText);
                
                while (matcher.find()) {
                    count++;
                    log.debug("욕설 감지: '{}' (위치: {})", word, matcher.start());
                }
            }

            log.info("로컬 욕설 필터링 완료: 총 {}회 감지 (욕설 단어 목록: {}개)", count, profanityWords.size());
            return count;

        } catch (Exception e) {
            log.error("욕설 판별 실패 (로컬 필터링)", e);
            return 0;
        }
    }

    /**
     * DB에서 욕설 단어 목록 가져오기 (캐시 사용)
     */
    private List<String> getProfanityWords() {
        long now = System.currentTimeMillis();
        
        // 캐시가 유효한 경우 재사용
        if (cachedProfanityWords != null && (now - lastCacheUpdate) < CACHE_TTL_MS) {
            return cachedProfanityWords;
        }

        // DB에서 조회
        List<ProfanityWord> words = profanityWordRepository.findByActiveTrue();
        cachedProfanityWords = words.stream()
                .map(ProfanityWord::getWord)
                .toList();
        lastCacheUpdate = now;

        log.debug("욕설 단어 목록 갱신: {}개", cachedProfanityWords.size());
        return cachedProfanityWords;
    }
}




