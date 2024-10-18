package com.pictalk.message.domain;

import com.pictalk.user.domain.User;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sender_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    private boolean isDeleted = false;
}
