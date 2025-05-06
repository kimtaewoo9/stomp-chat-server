package com.example.chatserver.chat.repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantsRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
