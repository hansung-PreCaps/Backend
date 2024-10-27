package com.pictalk.message.domain;

import com.pictalk.image.domain.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Sender sender;

    @OneToMany(mappedBy = "message")
    @Builder.Default
    private List<Receiver> receivers = new ArrayList<>();

    @OneToMany(mappedBy = "message")
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MessageStatus status; // [SCHEDULED, SENT, CANCELLED]

    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;

    @Builder.Default
    private boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Message(Sender sender, MessageStatus status, String content) {
        this.sender = sender;
        this.status = status;
        this.content = content;
    }
}
