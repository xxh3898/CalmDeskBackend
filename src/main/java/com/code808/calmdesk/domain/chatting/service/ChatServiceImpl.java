package com.code808.calmdesk.domain.chatting.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code808.calmdesk.domain.chatting.dto.ChatDto;
import com.code808.calmdesk.domain.chatting.entity.ChatMessage;
import com.code808.calmdesk.domain.chatting.entity.ChatRoom;
import com.code808.calmdesk.domain.chatting.entity.ChatRoomMember;
import com.code808.calmdesk.domain.chatting.repository.ChatMessageRepository;
import com.code808.calmdesk.domain.chatting.repository.ChatRoomMemberRepository;
import com.code808.calmdesk.domain.chatting.repository.ChatRoomRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

            int unreadCount;
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

        ChatRoomMember myMember = chatRoomMemberRepository.findByChatRoomIdAndMemberMemberIdWithLock(room.getId(), me.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("참여자 정보를 찾을 수 없습니다."));

        if (lastReadMessageId == null) {
            return;
        }

        // 기존 읽은 ID보다 작으면 업데이트 안 함
        Long currentLastReadId = myMember.getLastReadMessageId();
        Long previousId = currentLastReadId == null ? 0L : currentLastReadId;
        if (previousId >= lastReadMessageId) {
            return;
        }

        log.info("[markAsRead] User: {}, Room: {}, LastRead: {}, Prev: {}", email, roomId, lastReadMessageId, previousId);

        myMember.updateLastReadMessageId(lastReadMessageId);

        chatRoomMemberRepository.saveAndFlush(myMember); // 강제 플러시

        // [실시간 읽음 처리]
        // 클라이언트에게 읽음 이벤트를 전송하여, 해당 범위(previousId < id <= lastReadMessageId)의
        // 메시지 '안 읽은 사람 수'를 즉시 갱신하도록 유도합니다.
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId + "/read",
                new ChatDto.ChatReadEvent(previousId, lastReadMessageId));
    }

    @Override
    public List<ChatDto.ChatMessageRes> getChatHistory(String roomId, Long lastMessageId, int size) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, size);
        List<ChatMessage> messages;

        if (lastMessageId == null) {
            // 처음 로딩 시: 가장 최신 메시지 N개 가져오기
            messages = chatMessageRepository.findByChatRoomIdOrderByCreatedDateDesc(room.getId(), pageable);
        } else {
            // 스크롤 올릴 시: 해당 ID보다 이전 메시지 N개 가져오기
            messages = chatMessageRepository.findByChatRoomIdAndIdLessThanOrderByCreatedDateDesc(room.getId(), lastMessageId, pageable);
        }

        // 가져온 메시지를 시간순(과거->최신)으로 정렬
        messages.sort(java.util.Comparator.comparing(ChatMessage::getCreatedDate));

        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(room.getId());

        // [DEBUG] History 조회 시 멤버들의 lastReadMessageId 로그
        // members.forEach(m -> log.debug("[getChatHistory] Member: {}, LastRead: {}", m.getMember().getName(), m.getLastReadMessageId()));
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

    @Override

    public List<ChatDto.ChatMemberRes> getCompanyMembers(String email) {
        Member me = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (me.getCompany() == null) {
            throw new IllegalArgumentException("소속된 회사가 없습니다.");
        }

        List<Member> companyMembers = memberRepository.findAllByCompanyIdWithDepartmentAndRank(me.getCompany().getCompanyId());

        return companyMembers.stream()
                .filter(member -> !member.getMemberId().equals(me.getMemberId())) // 본인 제외
                .map(ChatDto.ChatMemberRes::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String createChatRoom(String myEmail, List<Long> targetMemberIds) {
        if (targetMemberIds == null || targetMemberIds.isEmpty()) {
            throw new IllegalArgumentException("대화 상대를 선택해주세요.");
        }

        Member me = memberRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다."));

        // 1:1 채팅인 경우 기존 로직 재사용
        if (targetMemberIds.size() == 1) {
            return createOrGetChatRoom(myEmail, targetMemberIds.get(0));
        }

        // 그룹 채팅: 기존 방 존재 여부 확인 (멤버 구성이 완전히 같은 방)
        // 1. 내가 참여 중인 방 목록 조회
        List<ChatRoomMember> myRooms = chatRoomMemberRepository.findByMemberMemberId(me.getMemberId());

        // 2. 검색 대상 멤버 ID 집합 (나 포함)
        java.util.Set<Long> targetIdsSet = new java.util.HashSet<>(targetMemberIds);
        targetIdsSet.add(me.getMemberId());

        // 3. 각 방의 멤버 구성을 확인
        for (ChatRoomMember myRoom : myRooms) {
            Long currentRoomId = myRoom.getChatRoom().getId();
            List<ChatRoomMember> roomMembers = chatRoomMemberRepository.findByChatRoomId(currentRoomId);

            // 멤버 수가 다르면 패스
            if (roomMembers.size() != targetIdsSet.size()) {
                continue;
            }

            // 멤버 구성이 일치하는지 확인
            boolean isSameMemberSet = roomMembers.stream()
                    .allMatch(rm -> targetIdsSet.contains(rm.getMember().getMemberId()));

            if (isSameMemberSet) {
                // 이미 존재하는 방이면 해당 방 ID 반환
                return myRoom.getChatRoom().getRoomId();
            }
        }

        // 새 그룹 채팅 생성
        List<Member> targets = memberRepository.findAllById(targetMemberIds);
        if (targets.size() != targetMemberIds.size()) {
            throw new IllegalArgumentException("일부 대화 상대를 찾을 수 없습니다.");
        }

        // 채팅방 이름 결정 (무조건 이름 조합)
        List<String> names = new java.util.ArrayList<>();
        names.add(me.getName());
        targets.forEach(m -> names.add(m.getName()));
        String finalRoomName = String.join(", ", names);

        ChatRoom chatRoom = ChatRoom.builder()
                .name(finalRoomName)
                .build();
        chatRoomRepository.save(chatRoom);

        // 참여자 추가 (나)
        ChatRoomMember myJoin = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(me)
                .roomNameAlias(makeGroupAlias(targets))
                .build();
        chatRoomMemberRepository.save(myJoin);

        // 참여자 추가 (상대방들)
        for (Member target : targets) {
            List<Member> others = new java.util.ArrayList<>(targets);
            others.remove(target);
            others.add(me);

            ChatRoomMember join = ChatRoomMember.builder()
                    .chatRoom(chatRoom)
                    .member(target)
                    .roomNameAlias(makeGroupAlias(others))
                    .build();
            chatRoomMemberRepository.save(join);
        }

        return chatRoom.getRoomId();
    }

    private String makeGroupAlias(List<Member> others) {
        // roomName 파라미터 무시하고 항상 자동 생성
        return others.stream()
                .map(Member::getName)
                .collect(Collectors.joining(", "));
    }
}
