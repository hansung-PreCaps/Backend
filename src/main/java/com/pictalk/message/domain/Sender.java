package com.pictalk.message.domain;

import com.pictalk.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sender")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sender_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "sender")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    private boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Sender(User user, String nickname, String phoneNumber) {
        this.user = user;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
}
