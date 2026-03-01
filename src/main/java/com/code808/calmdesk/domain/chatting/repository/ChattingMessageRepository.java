package com.code808.calmdesk.domain.chatting.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.chatting.entity.ChatMessage;

public interface ChattingMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm JOIN FETCH cm.sender WHERE cm.chatRoom.id = :chatRoomId ORDER BY cm.createdDate ASC")
    List<ChatMessage> findByChatRoomIdOrderByCreatedDateAsc(@Param("chatRoomId") Long chatRoomId);

    // 페이지네이션: 특정 ID보다 작은(이전) 메시지를 최신순으로 가져옴
    @Query(value = "SELECT cm FROM ChatMessage cm JOIN FETCH cm.sender WHERE cm.chatRoom.id = :chatRoomId AND cm.id < :id ORDER BY cm.createdDate DESC",
            countQuery = "SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId AND cm.id < :id")
    List<ChatMessage> findByChatRoomIdAndIdLessThanOrderByCreatedDateDesc(@Param("chatRoomId") Long chatRoomId, @Param("id") Long id, Pageable pageable);

    // 페이지네이션: 가장 최신 메시지를 가져옴
    @Query(value = "SELECT cm FROM ChatMessage cm JOIN FETCH cm.sender WHERE cm.chatRoom.id = :chatRoomId ORDER BY cm.createdDate DESC",
            countQuery = "SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId")
    List<ChatMessage> findByChatRoomIdOrderByCreatedDateDesc(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    int countByChatRoomIdAndIdGreaterThanAndIsDeletedFalse(Long chatRoomId, Long id);

    int countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long id);
}
