package com.meetapp.Services;

import com.meetapp.Model.ChatMessageEntity;
import com.meetapp.Repositories.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceJPA implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageServiceJPA(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void saveMessage(String senderName, String context) {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSenderName(senderName);
        chatMessageEntity.setContext(context);
        chatMessageRepository.save(chatMessageEntity);
    }

    @Override
    public List<ChatMessageEntity> getAllMessages() {
        return chatMessageRepository.getAllMessages();
    }
}
