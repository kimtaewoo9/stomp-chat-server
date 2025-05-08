package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomResponseDto;
import com.example.chatserver.chat.dto.MyChatRoomResponseDto;
import com.example.chatserver.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat/room/group/create")
    public ResponseEntity<?> createGroupChatRoom(@RequestParam String roomName) {
        chatService.createChatRoom(roomName);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/room/group/list")
    public ResponseEntity<?> getGroupChatRooms() {
        List<ChatRoomResponseDto> chatRooms = chatService.getGroupChatRooms();

        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/chat/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChat(@PathVariable Long roomId) {
        chatService.addParticipantToGroupChat(roomId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessageDto> chatMessageDto = chatService.getChatHistory(roomId);

        return ResponseEntity.ok(chatMessageDto);
    }

    // 메시지 읽음 처리 .. -> disconnect 시점에 채팅방에 있는 메시지들 전부 읽음 처리 .
    // 채팅룸 ID를 주면서 "나 그 채팅방에 있는 메시지 전부 읽었어" 라고 전달
    @PostMapping("/chat/room/{roomId}/read")
    public ResponseEntity<?> readAllMessages(@PathVariable Long roomId) {
        chatService.readAllMessages(roomId);

        return ResponseEntity.ok().build();
    }

    // 내 채팅방 목록 조회 dto roomId, roomName, 읽지 않은 메시지 개수, group 채팅 여부 .
    @GetMapping("/chat/my/rooms")
    public ResponseEntity<?> getMyChatRooms() {

        List<MyChatRoomResponseDto> myChatRoomListResponseDtos = chatService.getMyChatRooms();
        return ResponseEntity.ok(myChatRoomListResponseDtos);
    }

    @DeleteMapping("/chat/room/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChatroom(@PathVariable Long roomId) {
        chatService.leaveGroupChatRoom(roomId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateRoom(@RequestParam Long otherMemberId) {
        Long roomId = chatService.getOrCreatePrivateRoom(otherMemberId);
        return ResponseEntity.ok(roomId);
    }
}
