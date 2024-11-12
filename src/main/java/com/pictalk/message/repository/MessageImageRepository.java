package com.pictalk.message.repository;

import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.MessageImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageImageRepository extends JpaRepository<MessageImage, Long> {
    List<MessageImage> findAllByMessage(Message message);
    
}
