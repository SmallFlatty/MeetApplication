package com.meetapp.Services;

import com.meetapp.Model.ChatMessageEntity;

import java.util.List;

public interface ChatMessageService {

    void saveMessage(String senderName, String context);

    List<ChatMessageEntity> getAllMessages();
}
