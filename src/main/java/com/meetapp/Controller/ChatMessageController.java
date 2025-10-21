package com.meetapp.Controller;

import com.meetapp.Model.ChatMessageEntity;
import com.meetapp.Services.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*" , methods = {RequestMethod.GET, RequestMethod.POST})
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Autowired

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/save-message")
    void saveMessage(@RequestParam String senderName, @RequestParam String message ) {
        chatMessageService.saveMessage(senderName, message);
    }

    @GetMapping("/get-all-messages")
    List<ChatMessageEntity> getAllMessages() {
        return chatMessageService.getAllMessages();
    }
}
