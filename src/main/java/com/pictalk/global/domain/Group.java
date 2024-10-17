package com.pictalk.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group")
@Getter
@Setter
public class Group {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "group")
    private List<GroupReceiver> groupReceivers = new ArrayList<>();
}
