package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantsRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantsRepository chatParticipantsRepository;
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
        List<ChatParticipant> chatParticipants = chatParticipantsRepository.findByChatRoom(chatRoom);
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
}
