package com.pictalk.message.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.MessageImage;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.dto.MessageRequestDto;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto;
import com.pictalk.message.dto.MessageResponseDto.CancelMessageResponse;
import com.pictalk.message.dto.MessageResponseDto.MessageResponse;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.service.MessageServiceImpl;
import com.pictalk.message.service.facade.SendSendMessageFacade;
import com.pictalk.user.domain.User;
import com.pictalk.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageServiceImpl messageServiceImpl;
    private final SendSendMessageFacade sendMessageFacade;
    private final UserService userService;

    @PostMapping(value = "/sms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<SendMessageResponse> sendMMS(
            @AuthenticationPrincipal UserDetails authenticatedPrincipal,
            @RequestPart(name = "request") @Valid SendMessageRequest request,
            @RequestPart(name = "image", required = false) MultipartFile image) {

        User user = userService.getLoginUser(authenticatedPrincipal);
        MessageResponseDto.SendMessageResponse response = sendMessageFacade.sendMessage(request, user, image);
        return CommonResponse.onSuccess(response);
    }

    @PostMapping("/kakao")
    public CommonResponse<MessageResponseDto.SendMessageResponse> sendKakao(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                                                            @Valid @RequestBody MessageRequestDto.SendKakaoRequest request,
                                                                            MultipartFile image) {
        User user = userService.getLoginUser(authenticatedPrincipal);
        SendMessageResponse response = sendMessageFacade.sendMessage(request, user, image);
        return CommonResponse.onSuccess(response);
    }

    @GetMapping
    public CommonResponse<List<MessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetails authenticatedPrincipal) {
        String userEmail = authenticatedPrincipal.getUsername();
        List<Message> messages = messageServiceImpl.getMessages(userEmail);

        List<MessageResponse> messageResponses = messages.stream().map(message -> MessageResponse.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .to(getReceiversAsString(message.getReceivers()))
                .sendTime(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .messageImages(getImageUrls(message.getMessageImages()))
                .status(message.getStatus().toString())
                .build()
        ).toList();

        return CommonResponse.onSuccess(messageResponses);
    }

    private List<String> getImageUrls(List<MessageImage> messageImages) {
        return messageImages.stream()
                .map(messageImage -> messageImage.getImage().getImageUrl())
                .toList();
    }

    @GetMapping("/{message_id}")
    public CommonResponse<MessageResponse> getMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                                      @PathVariable("message_id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        Message message = messageServiceImpl.getMessage(messageId, userEmail);

        MessageResponse response = MessageResponse.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .to(getReceiversAsString(message.getReceivers()))
                .sendTime(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .status(message.getStatus().toString())
                .messageImages(message.getMessageImages().stream()
                        .map(messageImage -> messageImage.getImage().getImageUrl())
                        .collect(Collectors.toList()))
                .build();
        return CommonResponse.onSuccess(response);
    }

    @PatchMapping("/{message_id}")
    public CommonResponse<CancelMessageResponse> cancelScheduledMessage(
            @AuthenticationPrincipal UserDetails authenticatedPrincipal,
            @PathVariable("message_id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        CancelMessageResponse response = messageServiceImpl.cancelScheduledMessage(messageId, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @DeleteMapping("/{message_id}")
    public CommonResponse<Void> deleteMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                              @PathVariable("message_id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        messageServiceImpl.deleteMessage(messageId, userEmail);
        return CommonResponse.onSuccess(null);
    }

//    @PostMapping("/temp")
//    public CommonResponse<MessageResponseDto.TempMessageResponse> saveTempMessage(
//            @AuthenticationPrincipal UserDetails authenticatedPrincipal,
//            @Valid @RequestBody MessageRequestDto.TempMessageRequest request) {
//        String userEmail = authenticatedPrincipal.getUsername();
//        MessageResponseDto.TempMessageResponse response = messageServiceImpl.saveTempMessage(request, userEmail);
//        return CommonResponse.onSuccess(response);
//    }

    private String getReceiversAsString(List<Receiver> receivers) {
        return receivers.stream()
                .map(Receiver::getPhoneNumber)
                .collect(Collectors.joining(", "));
    }
}
