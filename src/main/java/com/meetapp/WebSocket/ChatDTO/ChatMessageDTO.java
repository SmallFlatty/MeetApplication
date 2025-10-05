package com.meetapp.WebSocket.ChatDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private String content;
    private String senderName;
    private String time;

    public ChatMessageDTO(String content, String senderName, String time) {
        this.content = content;
        this.senderName = senderName;
        this.time = time;
    }


}
