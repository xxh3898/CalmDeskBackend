package com.code808.calmdesk.domain.chat.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public String createOrGetChatRoom(Long myMemberId, Long targetMemberId) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findChatRoomByMemberIds(myMemberId, targetMemberId);
        if (existingRoom.isPresent()) {
            return existingRoom.get().getRoomId();
        }

        Member me = memberRepository.findById(myMemberId)
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다."));
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
            String lastMsg = messages.isEmpty() ? "대화 내용이 없습니다." : messages.get(messages.size() - 1).getContent();

            return ChatDto.ChatRoomRes.from(room, myRoomMember.getRoomNameAlias(), lastMsg,
                    messages.isEmpty() ? room.getCreatedDate() : messages.get(messages.size() - 1).getCreatedDate());
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessage saveMessage(ChatDto.ChatMessageReq request, String senderEmail) {
        ChatRoom room = chatRoomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Member sender = memberRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(request.getContent())
                .build();

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatDto.ChatMessageRes> getChatHistory(String roomId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        return chatMessageRepository.findByChatRoomIdOrderByCreatedDateAsc(room.getId())
                .stream()
                .map(ChatDto.ChatMessageRes::from)
                .collect(Collectors.toList());
    }
}
