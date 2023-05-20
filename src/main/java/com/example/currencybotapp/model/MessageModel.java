package com.example.currencybotapp.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "messages")
@Data
public class MessageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "message_text")
    private String messageText;

    @Column(name = "message_type")
    private String messageType;
}
