package com.code808.calmdesk.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.chat.entity.ChatRoomMember;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByMemberMemberId(Long memberId);

    Optional<ChatRoomMember> findByChatRoomIdAndMemberMemberId(Long chatRoomId, Long memberId);
}
