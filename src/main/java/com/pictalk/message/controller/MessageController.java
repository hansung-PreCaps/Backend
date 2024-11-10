package com.pictalk.message.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto.*;
import com.pictalk.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public CommonResponse<SendMessageResponse> sendMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal, @Valid @RequestBody SendMessageRequest request) {
        String userEmail = authenticatedPrincipal.getUsername();
        SendMessageResponse response = messageService.sendMessage(request, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @GetMapping
    public CommonResponse<List<MessageResponse>> getMessages(@AuthenticationPrincipal UserDetails authenticatedPrincipal) {
        String userEmail = authenticatedPrincipal.getUsername();
        List<MessageResponse> messages = messageService.getMessages(userEmail);
        return CommonResponse.onSuccess(messages);
    }

    @GetMapping("/{message-id}")
    public CommonResponse<MessageResponse> getMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal, @PathVariable("message-id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        MessageResponse response = messageService.getMessage(messageId, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @PatchMapping("/{message-id}")
    public CommonResponse<CancelMessageResponse> cancelScheduledMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal, @PathVariable("message-id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        CancelMessageResponse response = messageService.cancelScheduledMessage(messageId, userEmail);
        return CommonResponse.onSuccess(response);
    }

    @DeleteMapping("/{message-id}")
    public CommonResponse<Void> deleteMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal, @PathVariable("message-id") Long messageId) {
        String userEmail = authenticatedPrincipal.getUsername();
        messageService.deleteMessage(messageId, userEmail);
        return CommonResponse.onSuccess(null);
    }
}
