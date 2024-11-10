package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.*;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.repository.MessageRepository;
import com.pictalk.message.repository.SenderRepository;
import com.pictalk.user.domain.User;
import com.pictalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImmediateMessageService {
    private final MessageRepository messageRepository;
    private final SenderRepository senderRepository;
    private final UserRepository userRepository;

    @Transactional
    public SendMessageResponse sendImmediateMessage(SendMessageRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Sender sender = senderRepository.findSenderByPhoneNumber(request.getFrom())
                .orElseGet(() -> senderRepository.save(
                        Sender.builder()
                                .user(user)
                                .nickname(request.getFrom())
                                .phoneNumber(request.getFrom())
                                .build()
                ));

        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        List<Receiver> receivers = request.getTargets().stream()
                .map(target -> Receiver.builder()
                        .message(message)
                        .nickname(target.getName())
                        .phoneNumber(target.getTo())
                        .build())
                .collect(Collectors.toList());

        message.addReceivers(receivers);
        messageRepository.save(message);

        sendSms(message, receivers);

        return SendMessageResponse.builder()
                .externalMessageId(String.valueOf(message.getId()))
                .status("sent")
                .build();
    }

    private void sendSms(Message message, List<Receiver> receivers) {
        receivers.forEach(receiver -> {
            System.out.println("Sending SMS to " + receiver.getPhoneNumber() + ": " + message.getContent());
        });
    }
}
