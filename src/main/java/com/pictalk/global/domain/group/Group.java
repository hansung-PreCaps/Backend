package com.pictalk.global.domain.group;

import com.pictalk.global.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@Getter
@Setter
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groupId", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "group")
    private List<GroupReceiver> groupReceivers = new ArrayList<>();

    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    private boolean isDeleted = false;

    public void setUser(User user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    // groupReceivers에 요소를 추가하는 메서드 추가
    public void addGroupReceiver(GroupReceiver groupReceiver) {
        this.groupReceivers.add(groupReceiver);
        groupReceiver.setGroup(this); // 양방향 연관관계 설정
    }
}
