package com.pictalk.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message")
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Sender sender;

    private MessageStatus status; // [SCHEDULED, SENT, CANCELLED]
    private String content;
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "message")
    private List<Receiver> receivers = new ArrayList<>();

    @OneToMany(mappedBy = "message")
    private List<Image> images = new ArrayList<>();
}
