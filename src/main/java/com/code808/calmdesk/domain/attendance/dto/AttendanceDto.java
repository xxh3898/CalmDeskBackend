package com.code808.calmdesk.domain.attendance.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.code808.calmdesk.domain.attendance.entity.Attendance;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 출퇴근(Attendance) 관련 DTO - CompanyDto와 동일한 형태로 static 내부 클래스 + of() 구성
 */
public class AttendanceDto {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)", Locale.KOREAN);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN);

    /**
     * 프론트 Attendance 페이지 - 전체 기록 타임라인 / 일별 상세용 { id, day, date, clockIn,
     * clockOut, status, duration, note }
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceHistoryItemRes {

        @Schema(description = "ID", example = "101")
        private Long id;
        @Schema(description = "일(Day)", example = "24")
        private int day;
        @Schema(description = "날짜 (요일 포함)", example = "2026.02.24 (화)")
        private String date;
        @Schema(description = "출근 시간", example = "08:52")
        private String clockIn;
        @Schema(description = "퇴근 시간", example = "18:05")
        private String clockOut;
        @Schema(description = "출결 상태 (정상, 지각, 결근)", example = "정상")
        private String status;
        @Schema(description = "총 근무 시간", example = "9h 13m")
        private String duration;
        @Schema(description = "비고/특이사항", example = "특이사항 없음")
        private String note;

        public static AttendanceHistoryItemRes of(Attendance a) {
            String dateStr = a.getWorkDate().format(DATE_FORMAT);
            String clockInStr = a.getCheckIn() != null ? a.getCheckIn().format(TIME_FORMAT) : "-";
            String clockOutStr = a.getCheckOut() != null ? a.getCheckOut().format(TIME_FORMAT) : "-";
            String status = mapStatus(a.getAttendanceStatus());
            String duration = "-";
            if (a.getCheckIn() != null && a.getCheckOut() != null) {
                long minutes = Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes();
                duration = (minutes / 60) + "h " + (minutes % 60) + "m";
            }
            String note = a.getNote() != null && !a.getNote().isBlank() ? a.getNote() : "특이사항 없음";
            return AttendanceHistoryItemRes.builder()
                    .id(a.getAttendanceId())
                    .day(a.getWorkDate().getDayOfMonth())
                    .date(dateStr)
                    .clockIn(clockInStr)
                    .clockOut(clockOutStr)
                    .status(status)
                    .duration(duration)
                    .note(note)
                    .build();
        }
    }

    /**
     * 프론트 Attendance 페이지 - 요약 카드용 이번 달 출근 14/21일, 지각/결근 1건, 잔여 연차 12.5일, 이번 주
     * 근무 28.5시간
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceSummaryRes {

        @Schema(description = "이번 달 실제 출근 일수", example = "14")
        private int monthWorkDays;
        @Schema(description = "이번 달 영업일 총수", example = "21")
        private int monthTotalDays;
        @Schema(description = "지각/결근 횟수", example = "1")
        private int lateOrAbsenceCount;
        @Schema(description = "잔여 연차 일수", example = "12.5")
        private double remainingVacation;
        @Schema(description = "이번 주 총 근무 시간", example = "28.5")
        private double weekWorkHours;

        public static AttendanceSummaryRes of(int monthWorkDays, int monthTotalDays, int lateOrAbsenceCount,
                double remainingVacation, double weekWorkHours) {
            return AttendanceSummaryRes.builder()
                    .monthWorkDays(monthWorkDays)
                    .monthTotalDays(monthTotalDays)
                    .lateOrAbsenceCount(lateOrAbsenceCount)
                    .remainingVacation(remainingVacation)
                    .weekWorkHours(Math.round(weekWorkHours * 10) / 10.0)
                    .build();
        }
    }

    /**
     * 프론트 Attendance 페이지 - 휴가 현황용 { id, type, period, status, days } + 관리자 목록용
     * 필드
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveRequestItemRes {

        @Schema(description = "ID", example = "50")
        private Long id;
        @Schema(description = "휴가 종류 (연차, 반차, 워케이션)", example = "연차")
        private String type;
        @Schema(description = "휴가 기간", example = "2026.03.01 - 03.02")
        private String period;
        @Schema(description = "승인 상태 (승인대기, 승인완료, 반려)", example = "승인대기")
        private String status;
        @Schema(description = "사용 일수", example = "2일")
        private String days;
        @Schema(description = "신청자 이름", example = "홍길동")
        private String requestMemberName;
        @Schema(description = "부서명", example = "개발팀")
        private String departmentName;
        @Schema(description = "신청 사유", example = "개인 사유")
        private String reason;
        @Schema(description = "시작일", example = "2026-03-01")
        private LocalDate startDate;

        /**
         * 직원용 휴가 현황 (기본 필드만)
         */
        public static LeaveRequestItemRes of(Vacation v) {
            return LeaveRequestItemRes.builder()
                    .id(v.getVacationId())
                    .type(mapVacationType(v.getType()))
                    .period(formatPeriod(v))
                    .status(mapLeaveStatus(v.getStatus()))
                    .days(formatDays(v.getType(), v.getVacationDays()))
                    .build();
        }

        /**
         * 관리자용 휴가 목록 (신청자명, 부서명, 사유, 시작일 포함)
         */
        public static LeaveRequestItemRes ofForAdmin(Vacation v) {
            return LeaveRequestItemRes.builder()
                    .id(v.getVacationId())
                    .type(mapVacationType(v.getType()))
                    .period(formatPeriod(v))
                    .status(mapLeaveStatus(v.getStatus()))
                    .days(formatDays(v.getType(), v.getVacationDays()))
                    .requestMemberName(v.getRequestMember() != null ? v.getRequestMember().getName() : null)
                    .departmentName(v.getRequestMember() != null && v.getRequestMember().getDepartment() != null
                            ? v.getRequestMember().getDepartment().getDepartmentName() : null)
                    .reason(v.getReason())
                    .startDate(v.getStartDate() != null ? v.getStartDate().toLocalDate() : null)
                    .build();
        }
    }

    private static String mapStatus(Attendance.AttendanceStatus s) {
        return switch (s) {
            case ATTEND ->
                "정상";
            case LATE ->
                "지각";
            case ABSENCE ->
                "결근";
        };
    }

    private static String mapVacationType(Vacation.Type t) {
        return switch (t) {
            case ANNUAL ->
                "연차";
            case HALF ->
                "반차";
            case WORKCATION ->
                "워케이션";
        };
    }

    private static String mapLeaveStatus(CommonEnums.Status s) {
        return switch (s) {
            case Y ->
                "승인완료";
            case R ->
                "반려";
            default ->
                "승인대기";
        };
    }

    private static String formatPeriod(Vacation v) {
        if (v.getType() == Vacation.Type.HALF) {
            String dateStr = v.getStartDate().format(DATE_ONLY);
            String timeOfDay = v.getStartDate().getHour() < 13 ? "오전" : "오후";
            return dateStr + " (" + timeOfDay + ")";
        }
        String start = v.getStartDate().format(DATE_ONLY);
        String end = v.getEndDate().format(DATE_ONLY);
        return start.equals(end) ? start : start + " - " + end.substring(5);
    }

    private static String formatDays(Vacation.Type type, int vacationDays) {
        if (type == Vacation.Type.WORKCATION) {
            return "0.0일";
        }
        if (type == Vacation.Type.HALF) {
            return "0.5일";
        }
        return vacationDays + "일";
    }
}
