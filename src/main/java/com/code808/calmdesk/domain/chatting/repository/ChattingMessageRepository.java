package com.code808.calmdesk.domain.chatting.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.chatting.entity.ChatMessage;

public interface ChattingMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByCreatedDateAsc(Long chatRoomId);

    // 페이지네이션: 특정 ID보다 작은(이전) 메시지를 최신순으로 가져옴
    List<ChatMessage> findByChatRoomIdAndIdLessThanOrderByCreatedDateDesc(Long chatRoomId, Long id, Pageable pageable);

    // 페이지네이션: 가장 최신 메시지를 가져옴
    List<ChatMessage> findByChatRoomIdOrderByCreatedDateDesc(Long chatRoomId, Pageable pageable);

    int countByChatRoomIdAndIdGreaterThanAndIsDeletedFalse(Long chatRoomId, Long id);
}
