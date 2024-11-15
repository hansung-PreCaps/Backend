package com.pictalk.message.service;

import com.pictalk.message.domain.Receiver;
import com.pictalk.message.repository.ReceiverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiverService {

    private final ReceiverRepository receiverRepository;

    public Receiver findOrCreateReceiver(Long receiverId, String nickname, String phoneNumber) {
        return receiverRepository.findById(receiverId)
                .orElseGet(() -> receiverRepository.save(
                        Receiver.builder()
                                .id(receiverId)
                                .nickname(nickname)
                                .phoneNumber(phoneNumber)
                                .build()));
    }
}
