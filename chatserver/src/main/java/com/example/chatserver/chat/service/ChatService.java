package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomResponseDto;
import com.example.chatserver.chat.dto.MyChatRoomResponseDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ReadStatusRepository readStatusRepository;

    public ChatMessage saveMessage(Long roomId, ChatMessageDto chatMessageDto) {
        String message = chatMessageDto.getMessage();
        String senderEmail = chatMessageDto.getSenderEmail();

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
            () -> new EntityNotFoundException("chat room not found")
        );

        Member sender = memberRepository.findByEmail(senderEmail).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .member(sender)
            .content(message)
            .build();
        chatMessageRepository.save(chatMessage);

        // 읽음 여부 설정 .
        // TODO: chatParticipants 를 chatRoom에서 가져오도록 변경 ..(chatRoom.getParticipants)
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant chatParticipant : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                .chatRoom(chatRoom)
                .member(chatParticipant.getMember())
                .chatMessage(chatMessage)
                .isRead(chatParticipant.getMember().equals(sender)) // 보낸 사람이면 true로 세팅.
                .build();
            readStatusRepository.save(readStatus);
        }

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return savedMessage;
    }

    public void createChatRoom(String roomName) {
        ChatRoom chatRoom = ChatRoom.builder()
            .name(roomName)
            .isGroupChat("Y")
            .build();
        chatRoomRepository.save(chatRoom);

        // getName() -> 토큰에 포함된 subject 클레임을 가져옴 .
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        ChatParticipant chatParticipant = ChatParticipant.builder()
            .chatRoom(chatRoom)
            .member(member)
            .build();

        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatRoomResponseDto> getGroupChatRooms() {

        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");

        return chatRooms.stream()
            .map(chatRoom -> ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getName())
                .build())
            .toList();
    }

    public void addParticipantToGroupChat(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("chat room not found")
        );

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        // 이미 참여자인지 검증
        if(chatParticipantRepository.existsByChatRoomAndMember(chatRoom, member)){
            throw new IllegalArgumentException("Member already exists");
        }

        ChatParticipant chatParticipant = ChatParticipant.builder()
            .chatRoom(chatRoom)
            .member(member)
            .build();
        chatParticipantRepository.save(chatParticipant);
        chatRoom.getChatParticipants().add(chatParticipant);
    }

    public List<ChatMessageDto> getChatHistory(Long id) {
        // TODO 내가 참여한 시간 이후의 history만 가져와야함.
        // 내가 해당 채팅방의 참여자가 아닐 경우 에러 .
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("chat room not found")
        );

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );
        if(!chatParticipantRepository.existsByChatRoomAndMember(chatRoom, member)){
            throw new IllegalArgumentException("cannot access");
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        return chatMessages.stream()
            .map(message -> ChatMessageDto.builder()
                .senderEmail(message.getMember().getEmail())
                .message(message.getContent())
                .build())
            .toList();
    }

    public boolean isParticipant(String email, Long roomId) {
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
            () -> new EntityNotFoundException("chat room not found")
        );

        return chatParticipantRepository.existsByChatRoomAndMember(chatRoom, member);
    }

    public void readAllMessages(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("chat room not found")
        );

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        // 메시지에 대한 read status가 member 마다 존재함 . 1대다 관계.
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        for (ReadStatus readStatus : readStatuses) {
            readStatus.updateIsRead(); // 업데이트 따로 세이브 안해도됨 -> 더티 체킹 ..
        }
    }

    public List<MyChatRoomResponseDto> getMyChatRooms() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByMember(member);
        List<MyChatRoomResponseDto> myChatRoomResponseDtos = new ArrayList<>();
        for (ChatParticipant participant : chatParticipants) {
            ChatRoom currentChatRoom = participant.getChatRoom();

            Long unReadCount = readStatusRepository.
                countByChatRoomAndMemberAndIsReadFalse(currentChatRoom, member);
            MyChatRoomResponseDto responseDto = MyChatRoomResponseDto.builder()
                .id(currentChatRoom.getId())
                .roomName(currentChatRoom.getName())
                .isGroupChat(currentChatRoom.getIsGroupChat())
                .unReadCount(unReadCount)
                .build();
            myChatRoomResponseDtos.add(responseDto);
        }

        return myChatRoomResponseDtos;
    }

    public void leaveGroupChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
            () -> new EntityNotFoundException("chat room not found")
        );

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("member not found")
        );

        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("cannot delete");
        }

        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndMember(
            chatRoom, member).orElseThrow(
            () -> new EntityNotFoundException("you are not a participant")
        );
        chatRoom.getChatParticipants().remove(chatParticipant);
        chatParticipantRepository.delete(chatParticipant);

        int participantsCount = chatParticipantRepository.countByChatRoom(chatRoom);
        if(participantsCount == 0){
            chatRoomRepository.delete(chatRoom);
        }
    }
}
