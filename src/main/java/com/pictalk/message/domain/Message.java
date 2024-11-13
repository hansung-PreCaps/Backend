package com.pictalk.message.domain;

import com.pictalk.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "message")
    private List<MessageImage> messageImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MessageStatus status; // [SCHEDULED, SENT, CANCELLED]

    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;

    @Builder.Default
    private boolean deleted = false;

    public void addReceivers(List<Receiver> newReceivers) {
        this.receivers.addAll(newReceivers);
        newReceivers.forEach(receiver -> receiver.associateWithMessage(this));
    }

    public void addReceiver(Receiver receiver) {
        this.receivers.add(receiver);
        receiver.associateWithMessage(this);
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
