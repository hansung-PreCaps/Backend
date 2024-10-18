package com.pictalk.message.domain;

import com.pictalk.group.domain.GroupReceiver;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receiver")
@Getter
@Setter
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiver_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @OneToMany(mappedBy = "receiver")
    private List<GroupReceiver> groupReceivers = new ArrayList<>();

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    private boolean isDeleted;
}
