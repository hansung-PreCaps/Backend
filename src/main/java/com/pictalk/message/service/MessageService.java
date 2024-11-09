package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.*;
import com.pictalk.message.dto.MessageRequestDto.*;
import com.pictalk.message.dto.MessageResponseDto.*;
import com.pictalk.message.repository.MessageRepository;
import com.pictalk.message.repository.ReceiverRepository;
import com.pictalk.message.repository.SenderRepository;
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
    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;

    @Transactional
    public SendMessageResponse sendMessage(SendMessageRequest request, String userEmail) {
        // 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 발신자 조회 또는 생성
        Sender sender = senderRepository.findSenderByPhoneNumber(request.getFrom())
                .orElseGet(() -> senderRepository.save(
                        Sender.builder()
                                .user(user)
                                .nickname(request.getFrom())
                                .phoneNumber(request.getFrom())
                                .build()
                ));

        // 전송 시간에 따른 메시지 상태 결정
        LocalDateTime sendTime = LocalDateTime.parse(request.getSendTime());
        MessageStatus status = sendTime.isAfter(LocalDateTime.now()) ? MessageStatus.SCHEDULED : MessageStatus.SENT;

        // 메시지 생성
        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(status)
                .sentAt(sendTime)
                .build();

        // 수신자 생성
        List<Receiver> receivers = request.getTargets().stream()
                .map(target -> Receiver.builder()
                        .message(message)
                        .nickname(target.getName())
                        .phoneNumber(target.getTo())
                        .build())
                .collect(Collectors.toList());

        message.addReceivers(receivers);
        messageRepository.save(message);

        // 즉시 전송인 경우 SMS 발송
        if (status == MessageStatus.SENT) {
            sendSms(message, receivers);
        }

        return SendMessageResponse.builder()
                .externalMessageId(String.valueOf(message.getId()))
                .status(status.toString().toLowerCase())
                .build();
    }

    // 실제 SMS 전송 로직 (구현 필요)
    private void sendSms(Message message, List<Receiver> receivers) {
        receivers.forEach(receiver -> {
            System.out.println("Sending SMS to " + receiver.getPhoneNumber() + ": " + message.getContent());
        });
    }

    // 사용자의 모든 메시지 조회
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

    // 수신자 목록을 문자열로 변환
    private String getReceiversAsString(List<Receiver> receivers) {
        return receivers.stream()
                .map(Receiver::getPhoneNumber)
                .collect(Collectors.joining(", "));
    }

    // 특정 메시지 조회
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

    // 예약된 메시지 취소
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

    // 메시지 삭제 (소프트 삭제)
    @Transactional
    public void deleteMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message message = messageRepository.findByIdAndSenderUserAndDeletedFalse(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        message.softDelete();
        messageRepository.save(message);
    }
}
