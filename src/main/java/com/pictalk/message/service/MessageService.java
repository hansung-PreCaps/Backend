package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.*;
import com.pictalk.message.dto.MessageRequestDto.*;
import com.pictalk.message.dto.MessageResponseDto.*;
import com.pictalk.message.repository.MessageRepository;
import com.pictalk.message.repository.ReceiverRepository;
import com.pictalk.message.repository.SenderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;

    public SendMessageResponse sendMessage(SendMessageRequest request) {
        // Sender 조회 또는 생성
        Sender sender = senderRepository.findByPhoneNumber(request.getFrom())
                .orElseGet(() -> senderRepository.save(
                        Sender.builder()
                                .nickname(request.getFrom())
                                .phoneNumber(request.getFrom())
                                .build()
                ));

        // Message 생성
        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        // Receivers 생성
        List<Receiver> receivers = request.getTargets().stream()
                .map(target -> Receiver.builder()
                        .message(message)
                        .nickname(target.getName())
                        .phoneNumber(target.getTo())
                        .build())
                .collect(Collectors.toList());

        message.addReceivers(receivers);
        messageRepository.save(message);

        // 실제 SMS 전송 로직 구현 필요
        sendSms(message, receivers);

        SendMessageResponse response = SendMessageResponse.builder()
                .externalMessageId(String.valueOf(message.getId()))
                .status("sent")
                .build();

        return response;
    }

    private void sendSms(Message message, List<Receiver> receivers) {
        // 실제 SMS 전송 로직 구현 필요
        receivers.forEach(receiver -> {
            System.out.println("Sending SMS to " + receiver.getPhoneNumber() + ": " + message.getContent());
        });
    }

    public List<MessageResponse> getMessages() {
        List<Message> messages = messageRepository.findAllByIsDeletedFalse();

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

    public MessageResponse getMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        return MessageResponse.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .to(getReceiversAsString(message.getReceivers()))
                .sendTime(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .status(message.getStatus().toString())
                .build();
    }

    public CancelMessageResponse cancelScheduledMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        message.cancel();
        messageRepository.save(message);

        return CancelMessageResponse.builder()
                .messageId(message.getId())
                .status("reserve")
                .build();
    }

    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));
        message.softDelete();
        messageRepository.save(message);
    }
}
