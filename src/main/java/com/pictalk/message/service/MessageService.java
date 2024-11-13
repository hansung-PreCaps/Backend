package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.*;
import com.pictalk.message.dto.MessageRequestDto.*;
import com.pictalk.message.dto.MessageResponseDto.*;
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
public class MessageService {
    private final MessageRepository messageRepository;
    private final SenderRepository senderRepository;
    private final UserRepository userRepository;
    private final ImmediateMessageService immediateMessageService;
    private final ScheduledMessageService scheduledMessageService;

    @Transactional
    public SendMessageResponse sendMessage(SendMessageRequest request, String userEmail) {
        LocalDateTime sendTime = LocalDateTime.parse(request.getSendTime());
        LocalDateTime now = LocalDateTime.now();

        if (sendTime.isBefore(now)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        if (sendTime.isEqual(now)) {
            return immediateMessageService.sendImmediateMessage(request, userEmail);
        } else {
            return scheduledMessageService.scheduleMessage(request, userEmail);
        }
    }

    public List<MessageResponse> getMessages(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Message> messages = messageRepository.findAllByDeletedFalseAndSenderUser(user);

        return messages.stream().map(message -> MessageResponse.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .to(getReceiversAsString(message.getReceivers()))
                .sendTime(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .status(message.getStatus().toString())
                .build()
        ).collect(Collectors.toList());
    }

    private String getReceiversAsString(List<Receiver> receivers) {
        return receivers.stream()
                .map(Receiver::getPhoneNumber)
                .collect(Collectors.joining(", "));
    }

    public MessageResponse getMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message message = messageRepository.findByIdAndSenderUserAndDeletedFalse(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        return MessageResponse.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .to(getReceiversAsString(message.getReceivers()))
                .sendTime(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .status(message.getStatus().toString())
                .build();
    }

    @Transactional
    public void deleteMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message message = messageRepository.findByIdAndSenderUserAndDeletedFalse(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        message.softDelete();
        messageRepository.save(message);
    }

    @Transactional
    public CancelMessageResponse cancelScheduledMessage(Long messageId, String userEmail) {
        return scheduledMessageService.cancelScheduledMessage(messageId, userEmail);
    }

    @Transactional
    public TempMessageResponse saveTempMessage(TempMessageRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Sender sender = senderRepository.findSenderByPhoneNumber(request.getTo())
                .orElseGet(() -> senderRepository.save(
                        Sender.builder()
                                .user(user)
                                .phoneNumber(request.getTo())
                                .build()
                ));

        Message tempMessage = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(MessageStatus.TEMP)
                .sentAt(LocalDateTime.parse(request.getSendTime()))
                .build();

        Receiver receiver = Receiver.builder()
                .message(tempMessage)
                .phoneNumber(request.getTo())
                .build();

        tempMessage.addReceiver(receiver);
        Message savedMessage = messageRepository.save(tempMessage);

        return TempMessageResponse.builder()
                .messageId(savedMessage.getId())
                .status("temp")
                .build();
    }
}
