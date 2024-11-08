package com.pictalk.message.domain;

import com.pictalk.image.domain.Image;
import com.pictalk.global.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Sender sender;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Receiver> receivers = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
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
    private boolean deleted = false;

    public void addReceivers(List<Receiver> receivers) {
        this.receivers.addAll(receivers);
    }

    public void cancel() {
        if (this.status == MessageStatus.SCHEDULED) {
            this.status = MessageStatus.CANCELLED;
        }
    }

    public void softDelete() {
        this.deleted = true;
    }
}
