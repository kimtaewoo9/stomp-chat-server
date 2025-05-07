package com.example.chatserver.chat.repository;

import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
