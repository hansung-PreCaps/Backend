package com.pictalk.message.domain;

import com.pictalk.image.domain.Image;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Sender sender;

    @OneToMany(mappedBy = "message")
    private List<Receiver> receivers = new ArrayList<>();

    @OneToMany(mappedBy = "message")
    private List<Image> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MessageStatus status; // [SCHEDULED, SENT, CANCELLED]

    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    private boolean isDeleted = false;
}
