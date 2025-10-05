package com.meetapp.WebSocket;


import com.meetapp.WebSocket.ChatDTO.ChatMessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        if(chatMessageDTO.getTime() == null || chatMessageDTO.getTime().isEmpty()) {
            chatMessageDTO.setTime(Instant.now().toString());
        }

        simpMessagingTemplate.convertAndSend("/topic/chat", chatMessageDTO);
    }
}
