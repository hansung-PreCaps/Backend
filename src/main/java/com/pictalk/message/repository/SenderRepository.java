package com.pictalk.message.repository;

import com.pictalk.message.domain.Sender;
import com.pictalk.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderRepository extends JpaRepository<Sender, Long> {
    Optional<Sender> findSenderByPhoneNumber(String phoneNumber);

    Optional<Sender> findByPhoneNumberAndUser(String from, User user);
}
