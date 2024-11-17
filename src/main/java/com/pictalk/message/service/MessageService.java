package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto.CancelMessageResponse;
import com.pictalk.message.dto.MessageResponseDto.MessageResponse;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.repository.MessageRepository;
import com.pictalk.user.domain.User;
import com.pictalk.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
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

    public List<Message> getMessages(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Message> messages = messageRepository.findAllByDeletedFalseAndSenderUser(user);
        return messages;
    }

    public static String getReceiversAsString(List<Receiver> receivers) {
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
                .messageImages(message.getMessageImages().stream()
                        .map(messageImage -> messageImage.getImage().getImageUrl())
                        .collect(Collectors.toList()))
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
}
