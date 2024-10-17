package com.pictalk.global.domain;

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
    @GeneratedValue
    @Column(nullable = false)
    private Long receiverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "receiver")
    private List<GroupReceiver> groupReceivers = new ArrayList<>();
}
