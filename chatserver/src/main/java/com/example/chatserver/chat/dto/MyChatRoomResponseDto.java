package com.example.chatserver.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyChatRoomResponseDto {

    private Long roomId;
    private String roomName;
    private String isGroupChat;
    private Long unReadCount;
}
