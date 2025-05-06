package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomResponseDto;
import com.example.chatserver.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createGroupChatRoom(@RequestParam String roomName){
        chatService.createChatRoom(roomName);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/room/group/list")
    public ResponseEntity<?> getGroupChatRooms(){
        List<ChatRoomResponseDto> chatRooms = chatService.getGroupChatRooms();

        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/chat/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChat(@PathVariable Long id){
        chatService.addParticipantToGroupChat(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long id){
        List<ChatMessageDto> chatMessageDto = chatService.getChatHistory(id);

        return ResponseEntity.ok(chatMessageDto);
    }
}
