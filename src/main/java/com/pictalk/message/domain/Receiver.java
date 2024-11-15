package com.pictalk.message.domain;

import com.pictalk.global.common.BaseEntity;
import com.pictalk.group.domain.GroupReceiver;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receiver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Receiver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiver_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<GroupReceiver> groupReceivers = new ArrayList<>();

    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Builder
    public Receiver(String nickname, String phoneNumber) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

}
