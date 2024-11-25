package com.pictalk.infra;

import com.pictalk.global.component.PpurioClient;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.Sender;
import com.pictalk.message.dto.MessageRequestDto.FileDto;
import com.pictalk.message.dto.MessageRequestDto.Resend;
import com.pictalk.message.dto.MessageRequestDto.SendKakaoRequest;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.PpurioRequestDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
public class PpurioService {

    private final PpurioClient ppurioClient;
    private final RestTemplate restTemplate;

    public ResponseEntity<Map> getPpurioToken() {

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
        tokenHeaders.setBasicAuth(ppurioClient.getPpurioAccount(), ppurioClient.getApiKey());

        HttpEntity<String> tokenRequest = new HttpEntity<>(tokenHeaders);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(ppurioClient.getApiUrl() + "/v1/token", HttpMethod.POST,
                tokenRequest, Map.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

        return tokenResponse;
    }

    @Transactional
    public ResponseEntity<Map> sendMMS(List<FileDto> files, Sender sender, String accessToken, SendMessageRequest request) {

        HttpHeaders messageHeaders = new HttpHeaders();
        messageHeaders.setContentType(MediaType.APPLICATION_JSON);
        messageHeaders.setBearerAuth(accessToken);

        PpurioRequestDto.PpurioMMSRequestDto ppurioMMSRequestDto = PpurioRequestDto.PpurioMMSRequestDto.builder()
                .account(ppurioClient.getPpurioAccount())
                .messageType("MMS")
                .content(request.getContent())
                .from(request.getFrom())
                .duplicateFlag(request.getDuplicateFlag())
                .targetCount(request.getTargets().size())
                .targets(request.getTargets())
                .refKey(ppurioClient.getRefKey())
                .rejectType(request.getRejectType())
                .sendTime(request.getSendTime())
                .subject(request.getSubject())
                .files(files)
                .build();

        HttpEntity<PpurioRequestDto.PpurioMMSRequestDto> messageEntity = new HttpEntity<>(ppurioMMSRequestDto, messageHeaders);

        // 6. 메시지 전송 및 응답 처리
        ResponseEntity<Map> messageResponse = restTemplate.exchange(ppurioClient.getApiUrl() + "/v1/message",
                HttpMethod.POST,
                messageEntity,
                Map.class);

        if (messageResponse.getStatusCode() == HttpStatus.OK && messageResponse.getBody() != null) {
            return messageResponse;

        } else {
            System.out.println("Error sending message1");
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Map> sendKakao(List<Resend> resends, Sender sender, String accessToken, SendKakaoRequest request) {

        // 5. 메시지 객체 생성
        Message message = Message.builder()
                .sender(sender)
                .content(resends.get(0).getContent())
                .status(request.getStatus())
                .build();

        HttpHeaders messageHeaders = new HttpHeaders();
        messageHeaders.setContentType(MediaType.APPLICATION_JSON);
        messageHeaders.setBearerAuth(accessToken);

        PpurioRequestDto.PpurioKakaoRequestDto ppurioKakaoRequestDto = PpurioRequestDto.PpurioKakaoRequestDto.builder()
                .account(ppurioClient.getPpurioAccount())
                .messageType("ALT")
                .senderProfile("@뿌리오")
                .templateCode("ppur_2024020514240922798543242")
                .duplicateFlag(request.getDuplicateFlag())
                .targetCount(request.getTargets().size())
                .targets(request.getTargets())
                .refKey(ppurioClient.getRefKey())
                .sendTime(request.getSendTime())
                .isResend(request.getIsResend())
                .resend(resends)
                .build();

        HttpEntity<PpurioRequestDto.PpurioKakaoRequestDto> messageEntity = new HttpEntity<>(ppurioKakaoRequestDto, messageHeaders);

        // 6. 메시지 전송 및 응답 처리
        ResponseEntity<Map> messageResponse = restTemplate.exchange(ppurioClient.getApiUrl() + "/v1/kakao",
                HttpMethod.POST,
                messageEntity,
                Map.class);

        if (messageResponse.getStatusCode() == HttpStatus.OK && messageResponse.getBody() != null) {
            return messageResponse;

        } else {
            System.out.println("Error sending message1");
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
