package com.code808.calmdesk.domain.chatting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.chatting.entity.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByMemberMemberId(Long memberId);

    Optional<ChatRoomMember> findByChatRoomIdAndMemberMemberId(Long chatRoomId, Long memberId);

    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT crm FROM ChatRoomMember crm WHERE crm.chatRoom.id = :chatRoomId AND crm.member.memberId = :memberId")
    Optional<ChatRoomMember> findByChatRoomIdAndMemberMemberIdWithLock(@org.springframework.data.repository.query.Param("chatRoomId") Long chatRoomId, @org.springframework.data.repository.query.Param("memberId") Long memberId);
}
