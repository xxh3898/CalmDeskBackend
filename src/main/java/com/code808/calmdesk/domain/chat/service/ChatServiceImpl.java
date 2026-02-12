package com.code808.calmdesk.domain.chat.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.chat.dto.ChatDto;
import com.code808.calmdesk.domain.chat.entity.ChatMessage;
import com.code808.calmdesk.domain.chat.entity.ChatRoom;
import com.code808.calmdesk.domain.chat.entity.ChatRoomMember;
import com.code808.calmdesk.domain.chat.repository.ChatMessageRepository;
import com.code808.calmdesk.domain.chat.repository.ChatRoomMemberRepository;
import com.code808.calmdesk.domain.chat.repository.ChatRoomRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public String createOrGetChatRoom(String myEmail, Long targetMemberId) {
        Member me = memberRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다."));

        Optional<ChatRoom> existingRoom = chatRoomRepository.findChatRoomByMemberIds(me.getMemberId(), targetMemberId);
        if (existingRoom.isPresent()) {
            return existingRoom.get().getRoomId();
        }

        Member target = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("상대방 정보를 찾을 수 없습니다."));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(me.getName() + ", " + target.getName())
                .build();

        chatRoomRepository.save(chatRoom);

        ChatRoomMember myJoin = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(me)
                .roomNameAlias(target.getName())
                .build();

        ChatRoomMember targetJoin = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(target)
                .roomNameAlias(me.getName())
                .build();

        chatRoomMemberRepository.save(myJoin);
        chatRoomMemberRepository.save(targetJoin);

        return chatRoom.getRoomId();
    }

    @Override
    public List<ChatDto.ChatRoomRes> getMyChatRooms(String email) {
        Member me = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        List<ChatRoomMember> myRooms = chatRoomMemberRepository.findByMemberMemberId(me.getMemberId());

        return myRooms.stream().map(myRoomMember -> {
            ChatRoom room = myRoomMember.getChatRoom();
            List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedDateAsc(room.getId());

            // 삭제된 메시지 처리
            String lastMsg = "대화 내용이 없습니다.";
            if (!messages.isEmpty()) {
                ChatMessage last = messages.get(messages.size() - 1);
                lastMsg = last.isDeleted() ? "삭제된 메시지입니다." : last.getContent();
            }

            int unreadCount = 0;
            if (myRoomMember.getLastReadMessageId() != null) {
                unreadCount = chatMessageRepository.countByChatRoomIdAndIdGreaterThan(room.getId(), myRoomMember.getLastReadMessageId());
            } else {
                unreadCount = chatMessageRepository.countByChatRoomIdAndIdGreaterThan(room.getId(), 0L);
            }

            return ChatDto.ChatRoomRes.from(room, myRoomMember.getRoomNameAlias(), lastMsg,
                    messages.isEmpty() ? room.getCreatedDate() : messages.get(messages.size() - 1).getCreatedDate(), unreadCount);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatDto.ChatMessageRes saveMessage(ChatDto.ChatMessageReq request, String senderEmail) {
        ChatRoom room = chatRoomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Member sender = memberRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(request.getContent())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 보낸 사람은 자동으로 읽음 처리
        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(room.getId());
        ChatRoomMember myMember = members.stream()
                .filter(m -> m.getMember().getMemberId().equals(sender.getMemberId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("참여자 정보를 찾을 수 없습니다."));

        myMember.updateLastReadMessageId(savedMessage.getId());

        int unreadCount = members.size() - 1;

        ChatDto.ChatMessageRes response = ChatDto.ChatMessageRes.from(savedMessage, unreadCount);

        messagingTemplate.convertAndSend("/sub/chat/room/" + room.getRoomId(), response);

        for (ChatRoomMember member : members) {
            messagingTemplate.convertAndSend("/sub/chat/user/" + member.getMember().getEmail(), response);
        }

        return response;
    }

    @Override
    @Transactional
    public ChatMessage editMessage(Long messageId, ChatDto.ChatMessageEditReq request, String email) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (!message.getSender().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 메시지만 수정할 수 있습니다.");
        }

        message.updateContent(request.getContent());

        // 소켓 전송 (수정 이벤트)
        ChatDto.ChatMessageRes response = ChatDto.ChatMessageRes.from(message, 0); // 수정 시 읽음 카운트는 갱신 안 함
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoom().getRoomId(), response);

        return message;
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, String email) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        if (!message.getSender().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 메시지만 삭제할 수 있습니다.");
        }

        message.delete();

        // 소켓 전송 (삭제 이벤트 - 수정과 동일하게 처리하되 isDeleted가 true임)
        ChatDto.ChatMessageRes response = ChatDto.ChatMessageRes.from(message, 0);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoom().getRoomId(), response);
    }

    @Override
    @Transactional
    public void markAsRead(String roomId, String email, Long lastReadMessageId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Member me = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        ChatRoomMember myMember = chatRoomMemberRepository.findByChatRoomIdAndMemberMemberId(room.getId(), me.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("참여자 정보를 찾을 수 없습니다."));

        // 기존 읽은 ID보다 작으면 업데이트 안 함
        if (myMember.getLastReadMessageId() != null && myMember.getLastReadMessageId() >= lastReadMessageId) {
            return;
        }

        myMember.updateLastReadMessageId(lastReadMessageId);

        // [실시간 읽음 처리]
        // 클라이언트에게 읽음 이벤트를 전송하여, 해당 lastReadMessageId까지의 
        // 메시지 '안 읽은 사람 수'를 즉시 갱신하도록 유도합니다.
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId + "/read",
                new ChatDto.ChatReadReq(lastReadMessageId));
    }

    @Override
    public List<ChatDto.ChatMessageRes> getChatHistory(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedDateAsc(room.getId());
        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(room.getId());

        return messages.stream()
                .map(msg -> {
                    int unreadCount = 0;
                    for (ChatRoomMember member : members) {
                        Long lastReadId = member.getLastReadMessageId();
                        if (lastReadId == null || lastReadId < msg.getId()) {
                            unreadCount++;
                        }
                    }
                    return ChatDto.ChatMessageRes.from(msg, unreadCount);
                })
                .collect(Collectors.toList());
    }
}
