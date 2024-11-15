package com.pictalk.message.controller;

import static com.pictalk.message.service.MessageService.getReceiversAsString;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.MessageImage;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto.CancelMessageResponse;
import com.pictalk.message.dto.MessageResponseDto.MessageResponse;
import com.pictalk.message.dto.MessageResponseDto.SendMessageResponse;
import com.pictalk.message.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public CommonResponse<SendMessageResponse> sendMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                                           @Valid @RequestBody SendMessageRequest request) {
        String userEmail = authenticatedPrincipal.getUsername();
        SendMessageResponse response = messageService.sendMessage(request, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @GetMapping
    public CommonResponse<List<MessageResponse>> getMessages(@AuthenticationPrincipal UserDetails authenticatedPrincipal) {
        String userEmail = authenticatedPrincipal.getUsername();
        List<Message> messages = messageService.getMessages(userEmail);

        List<MessageResponse> messageResponses = messages.stream().map(message -> MessageResponse.builder()
                .messageId(message.getId())
                .content(message.getContent())
                .to(getReceiversAsString(message.getReceivers()))
                .sendTime(message.getSentAt() != null ? message.getSentAt().toString() : null)
                .messageImages(getImageUrls(message.getMessageImages()))
                .status(message.getStatus().toString())
                .build()
        ).collect(Collectors.toList());

        return CommonResponse.onSuccess(messageResponses);
    }

    private List<String> getImageUrls(List<MessageImage> messageImages) {
        return messageImages.stream()
                .map(messageImage -> messageImage.getImage().getImageUrl())
                .collect(Collectors.toList());
    }

    @GetMapping("/{message-id}")
    public CommonResponse<MessageResponse> getMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                                      @PathVariable("message-id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        MessageResponse response = messageService.getMessage(messageId, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @PatchMapping("/{message-id}")
    public CommonResponse<CancelMessageResponse> cancelScheduledMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                                                        @PathVariable("message-id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        CancelMessageResponse response = messageService.cancelScheduledMessage(messageId, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @DeleteMapping("/{message-id}")
    public CommonResponse<Void> deleteMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                              @PathVariable("message-id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        messageService.deleteMessage(messageId, userEmail);
        return CommonResponse.onSuccess(null);
    }
}
