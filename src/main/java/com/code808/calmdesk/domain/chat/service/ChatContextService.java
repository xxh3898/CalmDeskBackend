package com.code808.calmdesk.domain.chat.service;

import com.code808.calmdesk.domain.dashboard.dto.employee.EmployeeDashboardResponseDto;
import com.code808.calmdesk.domain.dashboard.service.employee.EmployeeDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 챗봇 시스템 프롬프트에 넣을 사용자별 DB 컨텍스트를 생성합니다.
 * 채팅 요청 시마다 최신 데이터를 조회합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatContextService {

    private final EmployeeDashboardService dashboardService;

    /**
     * memberId에 해당하는 사용자의 대시보드 데이터를 읽어
     * LLM이 이해할 수 있는 요약 문자열로 반환합니다.
     */
    public String buildContextForMember(Long memberId) {
        try {
            EmployeeDashboardResponseDto dto = dashboardService.getDashboardData(memberId);
            return formatContext(dto);
        } catch (Exception e) {
            log.warn("챗봇 컨텍스트 조회 실패 memberId={}", memberId, e);
            return "";
        }
    }

    private String formatContext(EmployeeDashboardResponseDto dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[현재 사용자 실시간 데이터]\n");

        if (dto.getUserProfile() != null) {
            sb.append("- 이름: ").append(dto.getUserProfile().getName()).append("\n");
        }

        if (dto.getAttendanceStats() != null) {
            var att = dto.getAttendanceStats();
            sb.append("- 출근율: ").append(att.getAttendanceRate() != null ? att.getAttendanceRate() : 0).append("%\n");
            sb.append("- 출근 상태: ").append(att.getCurrentStatus() != null ? att.getCurrentStatus() : "-").append("\n");
            if (att.getStatusMessage() != null && !att.getStatusMessage().isEmpty()) {
                sb.append("- 출근 메모: ").append(att.getStatusMessage()).append("\n");
            }
        }

        if (dto.getVacationStats() != null) {
            var vac = dto.getVacationStats();
            sb.append("- 연차 총 일수: ").append(vac.getTotalDays() != null ? vac.getTotalDays() : 0).append("일\n");
            sb.append("- 사용한 연차: ").append(vac.getUsedDays() != null ? vac.getUsedDays() : 0).append("일\n");
            sb.append("- 남은 연차: ").append(vac.getRemainingDays() != null ? vac.getRemainingDays() : 0).append("일\n");
        }

        if (dto.getStressStats() != null) {
            var stress = dto.getStressStats();
            sb.append("- 스트레스 점수: ").append(stress.getScore() != null ? stress.getScore() : 0).append("/100\n");
            sb.append("- 스트레스 상태: ").append(stress.getStatus() != null ? stress.getStatus() : "-").append("\n");
        }

        if (dto.getPointStats() != null && dto.getPointStats().getAmount() != null) {
            sb.append("- 보유 포인트: ").append(dto.getPointStats().getAmount()).append("P\n");
        }

        sb.append("\n위 데이터를 바탕으로 질문에 답변하세요. 사용자가 휴가, 출근, 스트레스, 포인트 등에 대해 물어보면 이 데이터를 활용해 구체적으로 답변하세요.");
        return sb.toString();
    }
}
