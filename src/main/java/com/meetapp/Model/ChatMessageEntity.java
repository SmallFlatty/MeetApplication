package com.meetapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "chat_messages")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "context")
    private String context;

    @Column(name = "send_time")
    private LocalDateTime sendTime =  LocalDateTime.now();
}
