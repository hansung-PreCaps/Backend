package com.pictalk.message.domain;

import com.pictalk.group.domain.GroupReceiver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receiver")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiver_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<GroupReceiver> groupReceivers = new ArrayList<>();

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    private boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Receiver(Message message, String nickname, String phoneNumber) {
        this.message = message;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
}
