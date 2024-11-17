package com.pictalk.group.repository;

import com.pictalk.group.domain.Group;
import com.pictalk.group.domain.GroupReceiver;
import com.pictalk.message.domain.Receiver;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupReceiverRepository extends JpaRepository<GroupReceiver, Long> {
    List<GroupReceiver> findAllByGroupId(Long groupId);

    Optional<GroupReceiver> findByGroupAndReceiver(Group group, Receiver receiver);
}
