package com.code808.calmdesk.domain.vacation.service;

import com.code808.calmdesk.domain.attendance.dto.AttendanceDto;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.vacation.dto.VacationDto;
import com.code808.calmdesk.domain.vacation.entity.Vacation;
import com.code808.calmdesk.domain.vacation.entity.VacationRest;
import com.code808.calmdesk.domain.vacation.repository.VacationRepository;
import com.code808.calmdesk.domain.vacation.repository.VacationRestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final VacationRestRepository vacationRestRepository;
    private final MemberRepository memberRepository;


    /**
     * 프론트 휴가 신청
     */
    @Transactional
    public VacationDto.VacationRequestRes requestVacation(Long memberId, VacationDto.VacationRequestReq req) {
        // 유효성 검사
        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new IllegalArgumentException("시작일과 종료일을 모두 입력해주세요.");
        }

        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("종료일은 시작일 이후여야 합니다.");
        }

        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // type 문자열을 Vacation.Type enum으로 변환
        Vacation.Type type = parseVacationType(req.getType());

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        int vacationDays;

        if (type == Vacation.Type.HALF) {
            // 반차: 같은 날이어야 함
            if (!req.getStartDate().equals(req.getEndDate())) {
                throw new IllegalArgumentException("반차는 시작일과 종료일이 같아야 합니다.");
            }
            // 반차: 오전/오후 선택에 따라 시간 설정
            String halfDayType = req.getHalfDayType();
            if (halfDayType != null && "오전".equals(halfDayType.trim())) {
                // 오전 반차: 09:00 ~ 13:00
                startDateTime = req.getStartDate().atTime(9, 0);
                endDateTime = req.getStartDate().atTime(13, 0);
            } else {
                // 오후 반차: 13:00 ~ 18:00 (기본값)
                startDateTime = req.getStartDate().atTime(13, 0);
                endDateTime = req.getStartDate().atTime(18, 0);
            }
            vacationDays = 1; // 반일 단위 (0.5일 = 1)
        } else if (type == Vacation.Type.WORKCATION) {
            // 워케이션: 09:00 ~ 18:00
            startDateTime = req.getStartDate().atTime(9, 0);
            endDateTime = req.getEndDate().atTime(18, 0);
            vacationDays = 0;
        } else {
            // 연차: 09:00 ~ 18:00
            startDateTime = req.getStartDate().atTime(9, 0);
            endDateTime = req.getEndDate().atTime(18, 0);
            // 시작일과 종료일 사이의 일수 계산 (포함)
            vacationDays = (int) req.getStartDate().datesUntil(req.getEndDate().plusDays(1)).count();
        }

        // reason이 null이거나 비어있으면 기본값 설정
        String reason = (req.getReason() == null || req.getReason().isBlank())
                ? "휴가 신청"
                : req.getReason();

        // 중복 체크: 같은 날짜에 대기 중이거나 승인된 휴가가 있는지 확인
        List<CommonEnums.Status> activeStatuses = List.of(CommonEnums.Status.N, CommonEnums.Status.Y);
        List<Vacation> overlappingVacations = vacationRepository.findOverlappingVacations(
                memberId, startDateTime, endDateTime, activeStatuses
        );

        if (!overlappingVacations.isEmpty()) {
            throw new IllegalArgumentException("해당 기간에 이미 신청되거나 승인된 휴가가 있습니다.");
        }

        // 남은 휴가 개수 체크 (워케이션은 제외)
        if (type != Vacation.Type.WORKCATION) {
            var restOpt = vacationRepository.findByMemberId(memberId);
            if (restOpt.isPresent()) {
                VacationRest vacationRest = restOpt.get();
                // totalCount는 일 단위, spentCount는 반차 단위
                double totalDays = vacationRest.getTotalCount();  // 일 단위
                double spentDays = vacationRest.getSpentCount() / 2.0;  // 반차 단위를 일 단위로 변환
                double remainingDays = totalDays - spentDays;

                // 신청하려는 휴가 일수
                double requestedDays = type == Vacation.Type.HALF ? 0.5 : vacationDays;

                if (remainingDays < requestedDays) {
                    throw new IllegalArgumentException(
                            String.format("남은 휴가가 부족합니다. (남은 휴가: %.1f일, 신청하려는 휴가: %.1f일)",
                                    remainingDays, requestedDays));
                }
            } else {
                // VacationRest가 없으면 기본값으로 체크
                throw new IllegalArgumentException("휴가 정보를 찾을 수 없습니다.");
            }
        }

        // Vacation 엔티티 생성 및 저장
        Vacation vacation = Vacation.builder()
                .type(type)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .reason(reason)
                .status(CommonEnums.Status.N) // 승인대기
                .vacationDays(vacationDays)
                .requestMember(member)
                .approverMember(null) // 신청 시점에는 승인자 없음
                .build();

        Vacation saved = vacationRepository.save(vacation);

        return VacationDto.VacationRequestRes.of(saved.getVacationId(), "휴가 신청이 완료되었습니다.");
    }

    @Transactional
    public VacationDto.VacationRequestRes approveVacation(Long vacationId, Long approverMemberId) {
        // 휴가 조회
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가입니다."));

        // 이미 승인된 경우
        if (vacation.getStatus() == CommonEnums.Status.Y) {
            throw new IllegalArgumentException("이미 승인된 휴가입니다.");
        }

        // 승인자 조회
        Member approver = memberRepository.findById(approverMemberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 승인자입니다."));

        // 휴가 승인 처리
        vacation.approve(approver);
        vacationRepository.save(vacation);

        // 워케이션이 아닌 경우에만 spentCount 증가
        if (vacation.getType() != Vacation.Type.WORKCATION) {
            var restOpt = vacationRepository.findByMemberId(vacation.getRequestMember().getMemberId());
            if (restOpt.isPresent()) {
                VacationRest vacationRest = restOpt.get();
                // spentCount는 반차 단위로 저장됨
                // 연차: 사용한 일수만큼 차감 (예: 4일 사용 → spentCount 8 증가 → 잔여 연차 4일 차감)
                // 반차: 0.5일 차감 (vacationDays = 1 → spentCount 1 증가 → 잔여 연차 0.5일 차감)
                int vacationDaysValue = vacation.getVacationDays();
                int countToAdd;
                if (vacation.getType() == Vacation.Type.HALF) {
                    // 반차: vacationDays = 1 → spentCount 1 증가 (0.5일 차감)
                    countToAdd = vacationDaysValue;
                } else {
                    // 연차: vacationDays = 실제 일수 → spentCount는 일수 * 2 증가
                    // 예: 4일 사용 → spentCount 8 증가 → (8 / 2.0) = 4일 차감
                    countToAdd = vacationDaysValue * 2;
                }
                vacationRest.addSpentCount(countToAdd);
                vacationRestRepository.save(vacationRest);
            }
        }

        return VacationDto.VacationRequestRes.of(vacation.getVacationId(), "휴가가 승인되었습니다.");
    }

    /**
     * 휴가 반려 (관리자용)
     */
    @Transactional
    public VacationDto.VacationRequestRes rejectVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가입니다."));
        if (vacation.getStatus() != CommonEnums.Status.N) {
            throw new IllegalArgumentException("대기 상태의 휴가만 반려할 수 있습니다.");
        }
        vacation.reject();
        vacationRepository.save(vacation);
        return VacationDto.VacationRequestRes.of(vacation.getVacationId(), "휴가가 반려되었습니다.");
    }

    /**
     * 휴가 취소 (직원용 - 승인 대기 상태만 취소 가능)
     */
    @Transactional
    public VacationDto.VacationRequestRes cancelVacation(Long vacationId, Long memberId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 휴가입니다."));

        // 본인이 신청한 휴가인지 확인
        if (!vacation.getRequestMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인이 신청한 휴가만 취소할 수 있습니다.");
        }

        // 승인 대기 상태만 취소 가능
        if (vacation.getStatus() != CommonEnums.Status.N) {
            throw new IllegalArgumentException("승인 대기 상태의 휴가만 취소할 수 있습니다.");
        }

        // 휴가 삭제
        vacationRepository.delete(vacation);

        return VacationDto.VacationRequestRes.of(vacationId, "휴가 신청이 취소되었습니다.");
    }

    /**
     * 관리자: 회사 소속 전체 휴가 신청 목록 (id, type, period, status, days, requestMemberName, departmentName)
     */
    @Transactional(readOnly = true)
    public List<AttendanceDto.LeaveRequestItemRes> getLeaveRequestsByCompany(Long companyId) {
        List<Vacation> list = vacationRepository.findByRequestMember_Company_CompanyIdOrderByStartDateDesc(companyId);
        return list.stream().map(AttendanceDto.LeaveRequestItemRes::ofForAdmin).collect(Collectors.toList());
    }

    /**
     * 프론트에서 받은 type 문자열을 Vacation.Type enum으로 변환
     */
    private Vacation.Type parseVacationType(String typeStr) {
        return switch (typeStr) {
            case "연차" -> Vacation.Type.ANNUAL;
            case "반차" -> Vacation.Type.HALF;
            case "워케이션" -> Vacation.Type.WORKCATION;
            default -> throw new IllegalArgumentException("유효하지 않은 휴가 종류입니다: " + typeStr);
        };
    }
}
