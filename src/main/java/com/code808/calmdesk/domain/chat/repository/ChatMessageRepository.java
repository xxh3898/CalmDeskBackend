package com.code808.calmdesk.domain.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code808.calmdesk.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByCreatedDateAsc(Long chatRoomId);

    int countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long id);
}
