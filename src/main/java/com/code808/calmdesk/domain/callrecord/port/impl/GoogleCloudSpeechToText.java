package com.code808.calmdesk.domain.callrecord.port.impl;

import com.code808.calmdesk.domain.callrecord.port.SpeechToTextPort;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Google Cloud Speech-to-Text API로 음성 → 텍스트 변환.
 * app.call-record.stt.provider=google-cloud 또는 미설정 시 사용 (기본값).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.call-record.stt.provider", havingValue = "google-cloud", matchIfMissing = true)
public class GoogleCloudSpeechToText implements SpeechToTextPort {

    @Value("${app.call-record.google-cloud.project-id:}")
    private String projectId;

    @Value("${app.call-record.google-cloud.location:global}")
    private String location;

    @Value("${app.call-record.google-cloud.credentials-path:}")
    private String credentialsPath;

    private SpeechClient speechClient;

    /**
     * SpeechClient를 지연 초기화 (필요할 때만 생성)
     */
    private SpeechClient getSpeechClient() throws IOException {
        if (speechClient == null) {
            String jsonPath = null;
            
            // 1순위: application-secret.yaml의 credentials-path
            if (credentialsPath != null && !credentialsPath.isBlank()) {
                jsonPath = credentialsPath;
                log.info("Google Cloud 인증: application-secret.yaml의 credentials-path 사용 - {}", jsonPath);
            } 
            // 2순위: 환경 변수 GOOGLE_APPLICATION_CREDENTIALS
            else {
                String envCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                if (envCredentials != null && !envCredentials.isBlank()) {
                    jsonPath = envCredentials;
                    log.info("Google Cloud 인증: 환경 변수 GOOGLE_APPLICATION_CREDENTIALS 사용 - {}", jsonPath);
                } else {
                    log.info("Google Cloud 인증: 기본 인증 방식 사용 (gcloud CLI 또는 GCP 환경)");
                }
            }
            
            // JSON 파일 경로가 있으면 직접 credentials 로드
            if (jsonPath != null) {
                File jsonFile = new File(jsonPath);
                if (!jsonFile.exists()) {
                    throw new IOException("Google Cloud 인증 JSON 파일을 찾을 수 없습니다: " + jsonPath);
                }
                
                log.info("Google Cloud 인증: JSON 파일에서 credentials 로드 중 - {}", jsonFile.getAbsolutePath());
                try (FileInputStream serviceAccountStream = new FileInputStream(jsonFile)) {
                    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
                    
                    // SpeechSettings를 사용하여 credentials 설정
                    SpeechSettings speechSettings = SpeechSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                            .build();
                    
                    speechClient = SpeechClient.create(speechSettings);
                    log.info("Google Cloud 인증: SpeechClient 생성 완료");
                }
            } else {
                // 기본 인증 방식 (환경 변수 또는 gcloud CLI)
                log.info("Google Cloud 인증: 기본 인증 방식으로 SpeechClient 생성");
                speechClient = SpeechClient.create();
            }
        }
        return speechClient;
    }

    @Override
    public String transcribe(byte[] audioBytes, String contentType) {
        if (audioBytes == null || audioBytes.length == 0) {
            log.warn("STT: 빈 오디오 파일");
            return "";
        }

        // 파일 크기 제한 (60MB - Google Cloud Speech-to-Text 제한)
        if (audioBytes.length > 60 * 1024 * 1024) {
            log.warn("STT: 파일 크기 초과 ({} bytes)", audioBytes.length);
            throw new RuntimeException("오디오 파일 크기가 너무 큽니다. 최대 60MB까지 지원됩니다.");
        }

        try {
            SpeechClient client = getSpeechClient();

            // MIME 타입을 RecognitionConfig의 AudioEncoding으로 변환
            RecognitionConfig.AudioEncoding encoding = getAudioEncoding(contentType);
            
            log.info("Google Cloud STT 시작: encoding={}, 파일 크기={} bytes", encoding, audioBytes.length);

            // RecognitionConfig 설정 (한국어 인식 정확도 최대화)
            RecognitionConfig.Builder configBuilder = RecognitionConfig.newBuilder()
                    .setEncoding(encoding)
                    .setLanguageCode("ko-KR") // 한국어
                    .setEnableAutomaticPunctuation(true) // 자동 구두점
                    .setModel("latest_long") // 최신 장문 모델
                    .setUseEnhanced(true) // 향상된 모델 사용 (정확도 향상)
                    .setEnableWordTimeOffsets(false) // 단어 시간 오프셋 비활성화 (속도 향상)
                    .setEnableWordConfidence(true); // 단어별 신뢰도 활성화 (정확도 확인용)

            // 일부 인코딩은 샘플레이트가 필요하지 않음 (WEBM_OPUS, MP3 등)
            // LINEAR16, FLAC 등은 샘플레이트 필요
            if (encoding == RecognitionConfig.AudioEncoding.LINEAR16 || 
                encoding == RecognitionConfig.AudioEncoding.FLAC) {
                configBuilder.setSampleRateHertz(16000); // 기본 샘플레이트
            }

            RecognitionConfig config = configBuilder.build();

            // 오디오 데이터 설정
            ByteString audioData = ByteString.copyFrom(audioBytes);
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioData)
                    .build();

            // 인식 요청 생성
            RecognizeRequest request = RecognizeRequest.newBuilder()
                    .setConfig(config)
                    .setAudio(audio)
                    .build();

            // API 호출
            RecognizeResponse response = client.recognize(request);
            List<SpeechRecognitionResult> results = response.getResultsList();

            log.info("Google Cloud STT 응답: 결과 개수={}", results.size());
            
            if (results.isEmpty()) {
                log.warn("STT: 인식 결과가 없습니다. 가능한 원인:");
                log.warn("  1. 오디오 파일에 음성이 없을 수 있습니다");
                log.warn("  2. 오디오 파일이 손상되었을 수 있습니다");
                log.warn("  3. 오디오 형식이 Google Cloud Speech-to-Text에서 지원하지 않을 수 있습니다");
                log.warn("  4. 오디오 길이가 너무 짧을 수 있습니다 (최소 0.1초 이상 필요)");
                log.warn("  5. 오디오 레벨이 너무 낮을 수 있습니다");
                return "";
            }

            // 첫 번째 결과의 대안 중 가장 신뢰도가 높은 것 선택
            StringBuilder transcript = new StringBuilder();
            double totalConfidence = 0;
            int resultCount = 0;
            
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcript.append(alternative.getTranscript()).append(" ");
                
                // 신뢰도 로깅 (getConfidence()는 항상 값을 반환하므로 try-catch로 처리)
                try {
                    double confidence = alternative.getConfidence();
                    totalConfidence += confidence;
                    resultCount++;
                    log.info("Google Cloud STT 결과 신뢰도: {}", confidence);
                } catch (Exception e) {
                    // 신뢰도 정보가 없는 경우 무시
                    log.debug("신뢰도 정보 없음");
                }
            }

            String text = transcript.toString().trim();
            
            if (resultCount > 0) {
                double avgConfidence = totalConfidence / resultCount;
                log.info("Google Cloud STT 성공: 텍스트 길이={}, 평균 신뢰도={} ({})", 
                    text.length(), 
                    avgConfidence,
                    avgConfidence >= 0.9 ? "우수" : avgConfidence >= 0.7 ? "양호" : avgConfidence >= 0.5 ? "보통" : "낮음");
            } else {
                log.info("Google Cloud STT 성공: 텍스트 길이={}", text.length());
            }
            
            // 전사 결과 미리보기 로깅
            if (text.length() > 0) {
                String preview = text.length() > 200 ? text.substring(0, 200) + "..." : text;
                log.info("Google Cloud STT 전사 결과 미리보기: {}", preview);
            }
            
            return text;

        } catch (IOException e) {
            log.error("Google Cloud STT 실패 (IO 오류): {}", e.getMessage(), e);
            throw new RuntimeException("음성 변환 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("Google Cloud STT 실패: {}", e.getMessage(), e);
            throw new RuntimeException("음성 변환 실패: " + (e.getMessage() != null ? e.getMessage() : "알 수 없는 오류"));
        }
    }

    /**
     * MIME 타입을 Google Cloud Speech-to-Text의 AudioEncoding으로 변환
     */
    private RecognitionConfig.AudioEncoding getAudioEncoding(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return RecognitionConfig.AudioEncoding.WEBM_OPUS; // 기본값
        }

        String lowerContentType = contentType.toLowerCase();
        
        if (lowerContentType.contains("webm") || lowerContentType.contains("opus")) {
            return RecognitionConfig.AudioEncoding.WEBM_OPUS;
        } else if (lowerContentType.contains("wav") || lowerContentType.contains("pcm")) {
            return RecognitionConfig.AudioEncoding.LINEAR16;
        } else if (lowerContentType.contains("flac")) {
            return RecognitionConfig.AudioEncoding.FLAC;
        } else if (lowerContentType.contains("mp3") || lowerContentType.contains("mpeg")) {
            return RecognitionConfig.AudioEncoding.MP3;
        } else if (lowerContentType.contains("ogg")) {
            return RecognitionConfig.AudioEncoding.OGG_OPUS;
        } else {
            log.warn("지원하지 않는 오디오 형식: {}, WEBM_OPUS로 시도합니다", contentType);
            return RecognitionConfig.AudioEncoding.WEBM_OPUS;
        }
    }

    /**
     * 리소스 정리
     */
    public void close() {
        if (speechClient != null) {
            try {
                speechClient.close();
            } catch (Exception e) {
                log.warn("SpeechClient 종료 중 오류: {}", e.getMessage());
            }
        }
    }
}

