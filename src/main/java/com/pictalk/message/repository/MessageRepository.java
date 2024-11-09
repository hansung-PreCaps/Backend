package com.pictalk.message.repository;

import com.pictalk.message.domain.Message;
import com.pictalk.user.domain.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 삭제되지 않은 특정 사용자의 모든 메시지 조회
    List<Message> findAllByDeletedFalseAndSenderUser(User user);

    // 삭제되지 않은 특정 사용자의 특정 메시지 조회
    Optional<Message> findByIdAndSenderUserAndDeletedFalse(Long id, User user);
}
