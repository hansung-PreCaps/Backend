package com.pictalk.message.service;

import com.pictalk.global.component.PpurioClient;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.infra.PpurioService;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.MessageStatus;
import com.pictalk.message.domain.Sender;
import com.pictalk.message.dto.MessageRequestDto;
import com.pictalk.message.dto.MessageRequestDto.TempMessageRequest;
import com.pictalk.message.dto.MessageResponseDto;
import com.pictalk.message.dto.MessageResponseDto.CancelMessageResponse;
import com.pictalk.message.repository.MessageRepository;
import com.pictalk.message.repository.ReceiverRepository;
import com.pictalk.message.repository.SenderRepository;
import com.pictalk.user.domain.User;
import com.pictalk.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {

    private final RestTemplate restTemplate;
    private final MessageRepository messageRepository;
    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final PpurioClient ppurioClient;
    private final PpurioService ppurioService;
    private final SenderService senderService;


    @Transactional(readOnly = true)
    public List<Message> getMessages(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return messageRepository.findAllBySenderUser(user);
    }


    @Transactional(readOnly = true)
    public Message getMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return messageRepository.findByIdAndSenderUser(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));
    }

    @Transactional
    public CancelMessageResponse cancelScheduledMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message message = messageRepository.findByIdAndSenderUser(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        if (message.getStatus() != MessageStatus.SCHEDULED) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        ppurioService.cancelMessage(user.getPpurioAccessToken(), message.getExternalMessageId());
        message.cancel();
        messageRepository.save(message);

        return CancelMessageResponse.builder().messageId(message.getId()).status(message.getStatus().toString())
                .build();
    }

    @Transactional
    public void deleteMessage(Long messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message message = messageRepository.findByIdAndSenderUser(messageId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));

        message.softDelete();
        messageRepository.save(message);
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MESSAGE_NOT_FOUND));
    }


    public List<Message> getMessageByStatus(String userEmail, String status) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return messageRepository.findBySenderUserAndStatus(user, MessageStatus.valueOf(status));
    }


    public void saveTempMessage(@Valid MessageRequestDto.TempMessageRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Sender sender = senderService.findOrCreateByUserAndPhoneNumber(user, request.getFrom());

        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(MessageStatus.TEMP)
                .build();

        saveMessage(message);
    }
}
