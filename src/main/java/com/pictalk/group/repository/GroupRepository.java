package com.pictalk.group.repository;

import com.pictalk.group.domain.Group;
import com.pictalk.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByUser(User user);
}
