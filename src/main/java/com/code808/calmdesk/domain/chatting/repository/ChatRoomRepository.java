package com.code808.calmdesk.domain.chatting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.chatting.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomId(String roomId);

    @Query("SELECT cr FROM ChatRoom cr "
            + "JOIN cr.members m1 "
            + "JOIN cr.members m2 "
            + "WHERE m1.member.memberId = :memberId1 AND m2.member.memberId = :memberId2")
    Optional<ChatRoom> findChatRoomByMemberIds(@Param("memberId1") Long memberId1, @Param("memberId2") Long memberId2);
}
