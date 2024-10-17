package com.pictalk.global.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "group_receiver")
@Getter
@Setter
public class GroupReceiver {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long groupReceiverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;
}
