package com.pictalk.group.domain;

import com.pictalk.message.domain.Receiver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_receiver")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_receiver_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    public GroupReceiver(Group group, Receiver receiver) {
        this.group = group;
        this.receiver = receiver;
    }
}
