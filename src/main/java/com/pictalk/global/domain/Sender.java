package com.pictalk.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sender")
@Getter
@Setter
public class Sender {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();
}
