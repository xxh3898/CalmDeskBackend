package com.code808.calmdesk.domain.callrecord.service;

import com.code808.calmdesk.domain.callrecord.dto.CallRecordDto;
import com.code808.calmdesk.domain.callrecord.entity.CallRecord;
import com.code808.calmdesk.domain.callrecord.port.ProfanityDetectionPort;
import com.code808.calmdesk.domain.callrecord.port.SpeechToTextPort;
import com.code808.calmdesk.domain.callrecord.repository.CallRecordRepository;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallRecordServiceImpl implements CallRecordService {

    @Value("${app.call-record.recording-dir:./recordings}")
    private String recordingDir;

    private final MemberRepository memberRepository;
    private final CallRecordRepository callRecordRepository;
    private final SpeechToTextPort speechToTextPort;
    private final ProfanityDetectionPort profanityDetectionPort;

    @Override
    @Transactional
    public Long uploadAndProcess(String employeeEmail, MultipartFile file, CallRecordDto.UploadRequest request) {
        Member employee = memberRepository.findEmailWithDetails(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
        Company company = employee.getCompany();
        if (company == null) {
            throw new IllegalArgumentException("회사에 소속된 사용자만 통화 기록을 등록할 수 있습니다.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("녹음 파일을 선택해 주세요.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("파일 읽기 실패: " + e.getMessage());
        }

        String relativePath = saveRecordingFile(bytes, file.getOriginalFilename(), company.getCompanyId(), employee.getMemberId());
        CallRecord record = CallRecord.builder()
                .company(company)
                .employee(employee)
                .customerPhone(normalizePhone(request.getCustomerPhone()))
                .callStartedAt(request.getCallStartedAt())
                .callEndedAt(request.getCallEndedAt())
                .recordingPath(relativePath)
                .profanityCount(0)
                .status(CallRecord.ProcessStatus.PENDING)
                .build();
        record = callRecordRepository.save(record);

        try {
            record.setStatus(CallRecord.ProcessStatus.PROCESSING);
            callRecordRepository.flush();

            String contentType = file.getContentType() != null ? file.getContentType() : "audio/webm";
            
            // 오디오 파일 크기 확인
            log.info("STT 처리 시작: 파일 크기={} bytes ({} KB), contentType={}", 
                bytes.length, bytes.length / 1024, contentType);
            
            if (bytes.length < 1000) {
                log.warn("오디오 파일 크기가 매우 작습니다 ({} bytes). 녹음이 제대로 되지 않았을 수 있습니다.", bytes.length);
            } else if (bytes.length < 10000) {
                log.warn("오디오 파일 크기가 작습니다 ({} bytes). 녹음 시간이 매우 짧거나 음성이 없을 수 있습니다.", bytes.length);
            }
            
            // 오디오 파일의 첫 몇 바이트 확인 (파일 형식 검증)
            if (bytes.length > 4) {
                String header = String.format("%02X %02X %02X %02X", 
                    bytes[0] & 0xFF, bytes[1] & 0xFF, bytes[2] & 0xFF, bytes[3] & 0xFF);
                log.info("오디오 파일 헤더 (첫 4바이트): {}", header);
                
                // WebM 파일 시그니처 확인 (1A 45 DF A3)
                // unsigned byte로 비교해야 함 (0xA3 = 163, signed로는 -93)
                if ((bytes[0] & 0xFF) == 0x1A && (bytes[1] & 0xFF) == 0x45 && 
                    (bytes[2] & 0xFF) == 0xDF && (bytes[3] & 0xFF) == 0xA3) {
                    log.info("WebM 파일 형식 확인됨");
                } else {
                    log.warn("WebM 파일 시그니처가 아닙니다. 파일이 손상되었을 수 있습니다.");
                    log.warn("예상: 1A 45 DF A3, 실제: {}", header);
                }
            }
            
            String transcript = speechToTextPort.transcribe(bytes, contentType);
            
            // 전사 결과 검증
            if (transcript == null || transcript.isBlank()) {
                log.warn("STT 결과가 비어있습니다. 오디오에 음성이 없을 수 있습니다.");
            } else {
                log.info("STT 결과: 길이={}, 미리보기={}", 
                    transcript.length(), 
                    transcript.length() > 100 ? transcript.substring(0, 100) + "..." : transcript);
            }
            
            record.setTranscript(transcript);

            int count = profanityDetectionPort.countProfanity(transcript);
            record.setProfanityCount(count);
            record.setStatus(CallRecord.ProcessStatus.DONE);
            record.setStatusMessage(null);
        } catch (Exception e) {
            log.warn("통화 기록 처리 실패: recordId={}", record.getId(), e);
            record.setStatus(CallRecord.ProcessStatus.FAILED);
            record.setStatusMessage(e.getMessage());
        }

        return record.getId();
    }

    private String saveRecordingFile(byte[] bytes, String originalFilename, Long companyId, Long employeeId) {
        try {
            Path base = Paths.get(recordingDir).toAbsolutePath().normalize();
            Files.createDirectories(base);
            String ext = "webm";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            }
            String filename = companyId + "_" + employeeId + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;
            Path target = base.resolve(filename);
            Files.write(target, bytes);
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("녹음 파일 저장 실패: " + e.getMessage());
        }
    }

    private static String normalizePhone(String phone) {
        if (phone == null) return "";
        return phone.trim().replaceAll("\\s+", "");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CallRecordDto.ListItem> list(String employeeEmail, String scope, Pageable pageable) {
        Member employee = memberRepository.findEmailWithDetails(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
        if (employee.getCompany() == null) {
            return Page.empty(pageable);
        }
        Long companyId = employee.getCompany().getCompanyId();
        Page<CallRecord> page = "all".equalsIgnoreCase(scope)
                ? callRecordRepository.findByCompany_CompanyIdOrderByCallStartedAtDesc(companyId, pageable)
                : callRecordRepository.findByEmployee_MemberIdOrderByCallStartedAtDesc(employee.getMemberId(), pageable);
        return page.map(CallRecordDto.ListItem::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecordDto.PhoneSearchItem> searchByPhone(String employeeEmail, String phone) {
        Member employee = memberRepository.findEmailWithDetails(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
        if (employee.getCompany() == null) {
            return List.of();
        }
        String normalized = normalizePhone(phone);
        if (normalized.isBlank()) {
            return List.of();
        }
        return CallRecordDto.PhoneSearchItem.fromList(
                callRecordRepository.findByCompanyIdAndCustomerPhoneOrderByCallStartedAtDesc(
                        employee.getCompany().getCompanyId(), normalized));
    }

    @Override
    @Transactional
    public boolean reprocessStt(String employeeEmail, Long recordId) {
        Member employee = memberRepository.findEmailWithDetails(employeeEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
        if (employee.getCompany() == null) {
            throw new IllegalArgumentException("회사에 소속된 사용자만 재처리할 수 있습니다.");
        }

        CallRecord record = callRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("통화 기록을 찾을 수 없습니다."));

        // 권한 확인: 본인 또는 같은 회사 소속
        if (!record.getEmployee().getMemberId().equals(employee.getMemberId()) &&
            !record.getCompany().getCompanyId().equals(employee.getCompany().getCompanyId())) {
            throw new IllegalArgumentException("재처리 권한이 없습니다.");
        }

        // 녹음 파일 읽기
        if (record.getRecordingPath() == null || record.getRecordingPath().isBlank()) {
            throw new IllegalArgumentException("녹음 파일을 찾을 수 없습니다.");
        }

        try {
            Path recordingPath = Paths.get(recordingDir).resolve(record.getRecordingPath());
            if (!Files.exists(recordingPath)) {
                throw new IllegalArgumentException("녹음 파일이 존재하지 않습니다: " + record.getRecordingPath());
            }

            byte[] bytes = Files.readAllBytes(recordingPath);
            String contentType = "audio/webm"; // 기본값

            // 파일 확장자로 contentType 추정
            String filename = record.getRecordingPath().toLowerCase();
            if (filename.endsWith(".ogg") || filename.endsWith(".opus")) {
                contentType = "audio/ogg";
            } else if (filename.endsWith(".webm")) {
                contentType = "audio/webm";
            } else if (filename.endsWith(".wav")) {
                contentType = "audio/wav";
            }

            record.setStatus(CallRecord.ProcessStatus.PROCESSING);
            callRecordRepository.flush();

            log.info("STT 재처리 시작: recordId={}, 파일 크기={} bytes", recordId, bytes.length);

            String transcript = speechToTextPort.transcribe(bytes, contentType);

            if (transcript == null || transcript.isBlank()) {
                log.warn("STT 재처리 결과가 비어있습니다: recordId={}", recordId);
                transcript = "";
            } else {
                log.info("STT 재처리 성공: recordId={}, 텍스트 길이={}", recordId, transcript.length());
            }

            record.setTranscript(transcript);

            int count = profanityDetectionPort.countProfanity(transcript);
            record.setProfanityCount(count);
            record.setStatus(CallRecord.ProcessStatus.DONE);
            record.setStatusMessage(null);

            return true;
        } catch (Exception e) {
            log.error("STT 재처리 실패: recordId={}", recordId, e);
            record.setStatus(CallRecord.ProcessStatus.FAILED);
            record.setStatusMessage(e.getMessage());
            throw new RuntimeException("STT 재처리 실패: " + e.getMessage());
        }
    }
}
