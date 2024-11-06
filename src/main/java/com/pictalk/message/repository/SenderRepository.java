package com.pictalk.message.repository;

import com.pictalk.message.domain.Sender;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderRepository extends JpaRepository<Sender, Long> {
    Optional<Sender> findByPhoneNumber(String phoneNumber);
}
