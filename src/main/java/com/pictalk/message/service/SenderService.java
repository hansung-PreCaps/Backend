package com.pictalk.message.service;

import com.pictalk.message.domain.Sender;
import com.pictalk.message.repository.SenderRepository;
import com.pictalk.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SenderService {
    private final SenderRepository senderRepository;

    @Transactional
    public Sender findOrCreateSender(String from, User user) {
        return senderRepository.findByPhoneNumberAndUser(from, user)
                .orElseGet(() -> senderRepository.save(Sender.builder().phoneNumber(from).user(user).build()));
    }
}
