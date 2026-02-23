package com.code808.calmdesk.domain.chat.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import com.code808.calmdesk.domain.chat.dto.ChatResponse;
import com.code808.calmdesk.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private static final String QUOTA_EXCEEDED_MESSAGE = "Gemini API 무료 할당량을 초과했습니다. 잠시(약 1분) 후 다시 시도해 주세요. "
            + "지속되면 Google AI Studio(https://aistudio.google.com)에서 할당량·결제 설정을 확인해 주세요.";

    private static final String EMPLOYEE_PAGE_GUIDE = """

            [Calm Desk 앱 페이지 안내 - 직원]
            사용자가 특정 기능을 사용하거나 신청하고 싶다고 하면, 아래 페이지로 안내하세요.
            - 근태 기록 조회 / 출근·퇴근 처리 / 근태 정정 신청 → '근태 관리' 페이지 (/app/attendance)
            - 연차·휴가 신청 / 휴가 현황 확인 → '근태 관리' 페이지 (/app/attendance)
            - 포인트 사용 / 기프티콘 구매 → '포인트 몰' 페이지 (/app/pointmall)
            - 내 정보 수정 / 비밀번호 변경 → '마이페이지' (/app/mypage)
            - 전체 업무 현황·통계 확인 → '대시보드' (/app/dashboard)
            - 부서원 정보 확인 → '부서' 페이지 (/app/department)
            - 심리 상담 신청 → '상담' 페이지 (/app/consultation)
            페이지를 안내할 때는 "왼쪽 메뉴(또는 상단 메뉴)에서 [페이지명]으로 이동하세요." 처럼 자연스럽게 안내하세요.
            """;

    private static final String ADMIN_PAGE_GUIDE = """

            [Calm Desk 앱 페이지 안내 - 관리자]
            사용자가 특정 기능을 사용하거나 확인하고 싶다고 하면, 아래 페이지로 안내하세요.
            - 직원 현황·팀 구성 관리 → '팀 관리' 페이지 (/app/teammanagement)
            - 직원 근태·스트레스 모니터링 → '모니터링' 페이지 (/app/monitoring)
            - 직원 근태 정정·휴가 신청 승인 → '신청 관리' 페이지 (/app/applications)
            - 기프티콘 등록·관리 / 구매 내역 확인 → '기프티콘 관리' 페이지 (/app/gifticons)
            - 내 정보 수정 / 비밀번호 변경 → '마이페이지' (/app/mypage)
            - 전체 현황·통계 확인 → '대시보드' (/app/dashboard)
            페이지를 안내할 때는 "왼쪽 메뉴(또는 상단 메뉴)에서 [페이지명]으로 이동하세요." 처럼 자연스럽게 안내하세요.
            """;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
            .withZone(ZoneId.of("Asia/Seoul"));

    private final ChatModel chatModel;
    private final ChatContextService chatContextService;

    @Override
    public ChatResponse chat(String userMessage, Long memberId, Member.Role role) {
        try {
            String today = ZonedDateTime.now().format(DATE_FORMAT);
            StringBuilder systemPromptBuilder = new StringBuilder();
            systemPromptBuilder.append(String.format("""
                    당신은 Calm Desk 업무용 챗봇입니다. 친절하고 간결하게 응답하세요.
                    오늘 날짜는 %s입니다. 이 정보는 참고용이며, 사용자가 날짜를 직접 물어보거나 스케줄·일정 관련 질문을 할 때만 답변에 날짜를 포함하세요.
                    일반적인 인사(예: 안녕, 안녕하세요, 하이 등)에는 날짜를 말하지 마세요.
                    """, today));

            if (memberId != null) {
                String dbContext = chatContextService.buildContextForMember(memberId);
                if (!dbContext.isEmpty()) {
                    systemPromptBuilder.append(dbContext);
                }
            }

            if (Member.Role.ADMIN.equals(role)) {
                systemPromptBuilder.append(ADMIN_PAGE_GUIDE);
            } else {
                systemPromptBuilder.append(EMPLOYEE_PAGE_GUIDE);
            }

            String systemPrompt = systemPromptBuilder.toString();

            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(userMessage)));
            org.springframework.ai.chat.model.ChatResponse springResponse = chatModel.call(prompt);
            String reply = springResponse.getResult().getOutput().getText();
            return ChatResponse.builder()
                    .reply(reply != null ? reply : "")
                    .build();
        } catch (Exception e) {
            String fullMsg = getFullMessage(e);
            if (fullMsg.contains("429") || fullMsg.contains("quota") || fullMsg.contains("Quota exceeded")) {
                log.warn("Gemini API 할당량 초과(429): {}", fullMsg);
                return ChatResponse.builder()
                        .reply(QUOTA_EXCEEDED_MESSAGE)
                        .build();
            }
            log.error("챗봇 처리 중 오류", e);
            return ChatResponse.builder()
                    .reply("챗봇 응답을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                    .build();
        }
    }

    private String getFullMessage(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (Throwable x = t; x != null; x = x.getCause()) {
            if (x.getMessage() != null) {
                if (sb.length() > 0)
                    sb.append(" / ");
                sb.append(x.getMessage());
            }
        }
        return sb.toString();
    }
}
