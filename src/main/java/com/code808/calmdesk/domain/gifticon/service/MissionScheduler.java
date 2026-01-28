package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.entity.MemberMission;
import com.code808.calmdesk.domain.gifticon.repository.MemberMissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionScheduler {

    private final MemberMissionRepository memberMissionRepository;

    // --------- 매일 초기화 되는 미션을 위한 로직
    /**
     * 매일 자정(00:00:00)에 실행되는 일일 미션 초기화 스케줄러
     * cron = "초 분 시 일 월 요일"
     */
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void resetDailyMissions() {
        log.info("일일 미션 초기화 시작...");

        // 1. 초기화 대상인 일일 미션(ATT_DAILY)만 조회
        List<MemberMission> dailyMissions = memberMissionRepository
                .findByMissionList_MissionCode("ATT_DAILY");

        for (MemberMission mm : dailyMissions) {
            // 2. 진행도 0으로 리셋, 상태를 N(진행중)으로 변경
            mm.resetMission();
        }

        log.info("총 {}건의 일일 미션이 초기화되었습니다.", dailyMissions.size());
    }


    // --------- 매월 초기화 되는 미션을 위한 로직
    @Transactional
    @Scheduled(cron = "0 * * * * *") // 매월 1일 자정
    public void resetMonthlyMissions() {
        log.info("월간 누적 미션 초기화 시작...");

        // 월간 미션 코드(ATT_RATE_80)를 가진 기록들만 조회
        List<MemberMission> monthlyMissions = memberMissionRepository
                .findByMissionList_MissionCode("ATT_RATE_80");

        for (MemberMission mm : monthlyMissions) {
            mm.resetMission(); // 진행도 0, 상태 N으로 리셋
        }

        log.info("총 {}건의 월간 미션이 초기화되었습니다.", monthlyMissions.size());
    }
}