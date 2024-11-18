package com.pictalk.message.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.MessageStatus;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.domain.Sender;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageRequestDto.Target;
import com.pictalk.message.dto.MessageRequestDto.TempMessageRequest;
import com.pictalk.message.dto.MessageResponseDto.CancelMessageResponse;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.dto.MessageResponseDto.TempMessageResponse;
import com.pictalk.message.repository.MessageRepository;
import com.pictalk.message.repository.ReceiverRepository;
import com.pictalk.message.repository.SenderRepository;
import com.pictalk.user.domain.User;
import com.pictalk.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MessageService {

    @Value("${ppurio.api.url}")
    private String apiUrl;

    @Value("${ppurio.api.key}")
    private String apiKey;

    @Value("${ppurio.api.account}")
    private String ppurioAccount;

    private final RestTemplate restTemplate;
    private final MessageRepository messageRepository;
    private final SenderRepository senderRepository;
    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;

    @Transactional
    public SendMessageResponse sendMessage(SendMessageRequest request, String userEmail) {
        // 1. 사용자 및 발신자 정보 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Sender sender = senderRepository.findSenderByPhoneNumber(request.getFrom()).orElseGet(
                () -> senderRepository.save(Sender.builder().user(user).phoneNumber(request.getFrom()).build()));

        // 2. 메시지 엔티티 생성
        Message message = Message.builder().sender(sender).content(request.getContent()).status(MessageStatus.SCHEDULED)
                .build();

        // 3. 수신자 정보 추가
        for (Target target : request.getTargets()) {
            Receiver receiver = Receiver.builder().phoneNumber(target.getTo()).nickname(target.getName()).build();
            message.addReceiver(receiver);
        }

        message = messageRepository.save(message);

        try {
            // 4. 토큰 발급 받기
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
            tokenHeaders.setBasicAuth(ppurioAccount, apiKey);

            HttpEntity<String> tokenRequest = new HttpEntity<>(tokenHeaders);

            ResponseEntity<Map> tokenResponse = restTemplate.exchange(apiUrl + "/v1/token", HttpMethod.POST,
                    tokenRequest, Map.class);

            if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
            }

            String accessToken = (String) tokenResponse.getBody().get("token");

            // 5. 메시지 전송 요청 준비
            HttpHeaders messageHeaders = new HttpHeaders();
            messageHeaders.setContentType(MediaType.APPLICATION_JSON);
            messageHeaders.setBearerAuth(accessToken);

            SendMessageRequest requestWithAccount = SendMessageRequest.builder().account(ppurioAccount)
                    .messageType(request.getMessageType()).content(request.getContent()).from(request.getFrom())
                    .duplicateFlag(request.getDuplicateFlag()).targetCount(request.getTargets().size())
                    .targets(request.getTargets()).refKey(request.getRefKey()).build();

            HttpEntity<SendMessageRequest> messageEntity = new HttpEntity<>(requestWithAccount, messageHeaders);

            // 6. 메시지 전송 및 응답 처리
            ResponseEntity<Map> messageResponse = restTemplate.exchange(apiUrl + "/v1/message", HttpMethod.POST,
                    messageEntity, Map.class);

            if (messageResponse.getStatusCode() == HttpStatus.OK && messageResponse.getBody() != null) {
                String messageKey = (String) messageResponse.getBody().get("messageKey");
                message.withExternalMessageId(messageKey);
                message = messageRepository.save(message);

                return SendMessageResponse.builder().externalMessageId(messageKey).status("success").build();
            } else {
                System.out.println("Error sending message1");
                throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            System.out.println("Error sending message2");
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public TempMessageResponse saveTempMessage(TempMessageRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Message tempMessage = Message.builder()
                .sender(user.getSenders().get(0))  // Assuming the user has at least one sender
                .content(request.getContent()).status(MessageStatus.TEMP).build();

        Receiver receiver = Receiver.builder().phoneNumber(request.getTo()).build();
        tempMessage.addReceiver(receiver);

        tempMessage = messageRepository.save(tempMessage);

        return TempMessageResponse.builder().messageId(tempMessage.getId()).status("temp_saved").build();
    }

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
}
