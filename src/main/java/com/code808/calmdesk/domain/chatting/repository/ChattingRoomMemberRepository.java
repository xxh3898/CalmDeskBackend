package com.code808.calmdesk.domain.chatting.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.chatting.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChattingRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByMemberMemberId(Long memberId);

    Optional<ChatRoomMember> findByChatRoomIdAndMemberMemberId(Long chatRoomId, Long memberId);

    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT crm FROM ChatRoomMember crm WHERE crm.chatRoom.id = :chatRoomId AND crm.member.memberId = :memberId")
    Optional<ChatRoomMember> findByChatRoomIdAndMemberMemberIdWithLock(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);
}
