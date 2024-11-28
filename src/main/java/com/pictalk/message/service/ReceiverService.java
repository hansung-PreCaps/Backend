package com.pictalk.message.service;

import com.pictalk.group.dto.GroupRequestDto.GroupReceiverDto;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.dto.MessageRequestDto.Target;
import com.pictalk.message.repository.ReceiverRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiverService {

    private final ReceiverRepository receiverRepository;

    public List<Receiver> createReceivers(List<GroupReceiverDto> groupReceivers) {
        return receiverRepository.saveAll(groupReceivers.stream()
                .map(groupReceiverDto -> Receiver.builder()
                        .nickname(groupReceiverDto.getNickname())
                        .phoneNumber(groupReceiverDto.getPhoneNumber())
                        .build())
                .collect(Collectors.toList()));
    }

    public List<Receiver> findOrCreateReceivers(List<Target> targets) {
        List<Receiver> receivers = new ArrayList<>();

        for (Target target : targets) {
            Receiver receiver =  receiverRepository.findByPhoneNumber(target.getTo())
                    .orElseGet(() -> receiverRepository.save(Receiver.builder()
                            .phoneNumber(target.getTo())
                            .nickname(target.getName())
                            .build()));

            receivers.add(receiver);
        }

        return receivers;
    }
}
