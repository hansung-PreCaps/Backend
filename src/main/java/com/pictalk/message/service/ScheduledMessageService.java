package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.*;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.dto.MessageResponseDto.CancelMessageResponse;
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
public class ScheduledMessageService {
    private final MessageRepository messageRepository;
    private final SenderRepository senderRepository;
    private final UserRepository userRepository;

    @Transactional
    public SendMessageResponse scheduleMessage(SendMessageRequest request, String userEmail) {
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

        LocalDateTime scheduledTime = LocalDateTime.parse(request.getSendTime());

        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(MessageStatus.SCHEDULED)
                .sentAt(scheduledTime)
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

        return SendMessageResponse.builder()
                .externalMessageId(String.valueOf(message.getId()))
                .status("scheduled")
                .build();
    }

    @Transactional
    public CancelMessageResponse cancelScheduledMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message message = messageRepository.findByIdAndSenderUserAndDeletedFalse(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        if (message.getStatus() != MessageStatus.SCHEDULED) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        message.cancel();
        messageRepository.save(message);

        return CancelMessageResponse.builder()
                .messageId(message.getId())
                .status("cancelled")
                .build();
    }
}
