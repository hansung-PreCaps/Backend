package com.pictalk.message.repository;

import com.pictalk.message.domain.Receiver;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    Optional<Receiver> findByPhoneNumber(String to);
}
