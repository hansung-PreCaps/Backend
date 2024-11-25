package com.pictalk.message.service.facade;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.global.util.ImageUtil;
import com.pictalk.global.vo.Image;
import com.pictalk.infra.PpurioService;
import com.pictalk.infra.S3Uploader;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.domain.Sender;
import com.pictalk.message.dto.MessageRequestDto.FileDto;
import com.pictalk.message.dto.MessageRequestDto.Resend;
import com.pictalk.message.dto.MessageRequestDto.Resend.ResendFile;
import com.pictalk.message.dto.MessageRequestDto.SendKakaoRequest;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageRequestDto.Target;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.service.MessageImageService;
import com.pictalk.message.service.MessageServiceImpl;
import com.pictalk.message.service.ReceiverService;
import com.pictalk.message.service.SenderService;
import com.pictalk.user.domain.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SendSendMessageFacade implements SendMessageService<Object, SendMessageResponse> {
    private final MessageServiceImpl messageServiceImpl;
    private final MessageImageService messageImageService;
    private final ReceiverService receiverService;
    private final SenderService senderService;
    private final PpurioService ppurioService;
    private final S3Uploader s3Uploader;

    @Override
    public SendMessageResponse sendMessage(Object request, User user, MultipartFile image) {
        if (request instanceof SendMessageRequest) {
            // MMS 로직
            SendMessageRequest mmsRequest = (SendMessageRequest) request;
            return processMms(mmsRequest, user, image);
        } else if (request instanceof SendKakaoRequest) {
            // Kakao 로직
            SendKakaoRequest kakaoRequest = (SendKakaoRequest) request;
            return processKakao(kakaoRequest, user, image);
        }
        throw new GeneralException(ErrorStatus.INVALID_REQUEST);
    }

    // MMS 로직
    @Transactional
    public SendMessageResponse processMms(SendMessageRequest request, User user, MultipartFile image) {
        // 발신자 찾기 -> 없으면 새로 생성
        Sender sender = senderService.findOrCreateSender(request.getFrom(), user);

        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .status(request.getStatus())
                .build();

        for (Target target : request.getTargets()) {
            Receiver receiver = Receiver.builder()
                    .phoneNumber(target.getTo())
                    .nickname(target.getName())
                    .build();

            message.addReceiver(receiver);
        }

        // 수신자 생성
        receiverService.findOrCreateReceivers(request.getTargets());

//        List<FileDto> files = images.stream()
//                .map(ImageUtil::convertMultipartFileToFileDto)
//                .collect(Collectors.toList());
        FileDto file = ImageUtil.convertMultipartFileToFileDto(image);

        // 5. 뿌리오 메시지 전송
        ResponseEntity<Map> messageResponse = ppurioService.sendMMS((List<FileDto>) file, sender, user.getPpurioAccessToken(), request);

        String messageKey = (String) messageResponse.getBody().get("messageKey");

        messageServiceImpl.saveMessage(message, messageKey);
        messageImageService.createFiletoImage(message, image);

        return SendMessageResponse
                .builder()
                .externalMessageId(messageKey)
                .status("success")
                .build();
    }


    // Kakao 로직
    @Transactional
    public SendMessageResponse processKakao(SendKakaoRequest request, User user, MultipartFile multipartFile) {

        // 발신자 찾기 -> 없으면 새로 생성
        Sender sender = senderService.findOrCreateSender("Kakao", user);

        // 수신자 생성
        receiverService.findOrCreateReceivers(request.getTargets());

        Message message = Message.builder()
                .sender(sender)
                .content(request.getResends().get(0).getContent())
                .status(request.getStatus())
                .build();

        // 3. 수신자 정보 추가
        for (Target target : request.getTargets()) {
            Receiver receiver = Receiver.builder()
                    .phoneNumber(target.getTo())
                    .nickname(target.getName())
                    .build();

            message.addReceiver(receiver);
        }

//        List<Image> images = multipartFiles.stream()
//                .map(s3Uploader::uploadImage) // uploadImage 메서드 호출
//                .toList();

        Image image = s3Uploader.uploadImage(multipartFile);
//        List<ResendFile> files = images.stream()
//                .map(ImageUtil::convertMultipartFileToResendFile)
//                .toList();

        ResendFile file = ImageUtil.convertMultipartFileToResendFile(image);

        List<Resend> resend = request.getResends().stream()
                .map(existingResend -> Resend.builder()
                        .content(existingResend.getContent()) // 기존 Resend의 content 사용
                        .from(existingResend.getFrom()) // 요청에서 공통으로 가져온 from 값 사용
                        .subject(existingResend.getSubject()) // 요청에서 subject 사용
                        .files((List<ResendFile>) file) // 변환된 files 사용
                        .build())
                .collect(Collectors.toList());

        ResponseEntity<Map> messageResponse = ppurioService.sendKakao(resend, sender, user.getPpurioAccessToken(), request);

        String messageKey = (String) messageResponse.getBody().get("messageKey");

        messageServiceImpl.saveMessage(message, messageKey);

        messageImageService.createImage(message, image);

        return SendMessageResponse
                .builder()
                .externalMessageId(messageKey)
                .status("success")
                .build();

    }

}
