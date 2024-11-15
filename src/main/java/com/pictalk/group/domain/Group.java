package com.pictalk.group.domain;

import com.pictalk.global.common.BaseEntity;
import com.pictalk.user.domain.User;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`groups`")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "group")
    private List<GroupReceiver> groupReceivers = new ArrayList<>();

    private String name;

    @Builder
    public Group(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public void addGroupReceiver(GroupReceiver groupReceiver) {
        groupReceivers.add(groupReceiver);
    }

    public void update(String groupName) {
        this.name = groupName;
    }
}
