package com.pictalk.message.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.message.dto.MessageRequestDto;
import com.pictalk.message.dto.MessageRequestDto.SendMessageRequest;
import com.pictalk.message.dto.MessageResponseDto;
import com.pictalk.message.dto.MessageResponseDto.*;
import com.pictalk.message.dto.MessageRequestDto.TempMessageRequest;
import com.pictalk.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
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
    public CommonResponse<MessageResponseDto.SendMessageResponse> sendMessage(@AuthenticationPrincipal UserDetails authenticatedPrincipal, @Valid @RequestBody MessageRequestDto.SendMessageRequest request) {
        String userEmail = authenticatedPrincipal.getUsername();
        MessageResponseDto.SendMessageResponse response = messageService.sendMessage(request, userEmail);
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

    @PostMapping("/temp")
    public CommonResponse<MessageResponseDto.TempMessageResponse> saveTempMessage(
            @AuthenticationPrincipal UserDetails authenticatedPrincipal,
            @Valid @RequestBody MessageRequestDto.TempMessageRequest request) {
        String userEmail = authenticatedPrincipal.getUsername();
        MessageResponseDto.TempMessageResponse response = messageService.saveTempMessage(request, userEmail);
        return CommonResponse.onSuccess(response);
    }
}
