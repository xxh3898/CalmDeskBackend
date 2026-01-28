package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.MemberMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberMissionRepository extends JpaRepository<MemberMission, Long> {

    // 1. [수정] findByUser_UserId -> findByMember_MemberId
    // 엔티티에 필드명이 'member'로 되어 있으므로 Member_MemberId를 사용해야 합니다.
    List<MemberMission> findByMember_MemberId(Long memberId);

    // 2. [수정] 복합 조회 (Query 사용 추천)
    @Query("SELECT mm FROM MemberMission mm " +
            "WHERE mm.member.memberId = :memberId " +
            "AND mm.missionList.missionListId = :missionId")
    Optional<MemberMission> findMemberMission(@Param("memberId") Long memberId,
                                              @Param("missionId") Long missionId);


    Optional<MemberMission> findByMember_MemberIdAndMissionList_MissionListId(Long memberId, Long missionListId);


    // 스케줄러에서 일일 미션 초기화 시 사용 (필요 시)
    List<MemberMission> findByMissionList_MissionCode(String missionCode);
}