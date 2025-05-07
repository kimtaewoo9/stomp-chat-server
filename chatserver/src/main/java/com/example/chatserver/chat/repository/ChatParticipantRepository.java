package com.example.chatserver.chat.repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatParticipant> findByMember(Member member);

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    int countByChatRoom(ChatRoom chatRoom);

    // 나의 ID를 기준으로 참여자 목록을 필터링하고, 상대방의 ID 기준으로 참여자 목록 필터링 .
    // where 조건에서 데이터를 필터링함 .
    @Query(
        "SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 ON cp1.chatRoom.id = cp2.chatRoom.id WHERE cp1.member.id =:myId AND cp2.member.id =:otherMemberId AND cp1.chatRoom.isGroupChat = 'N'")
    Optional<ChatRoom> existsPrivateRoom(
        @Param("myId") Long myId,
        @Param("otherMemberId") Long otherMemberId);
}
