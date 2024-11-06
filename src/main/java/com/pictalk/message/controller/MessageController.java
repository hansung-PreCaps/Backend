package com.pictalk.message.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.message.dto.MessageDto.*;
import com.pictalk.message.service.MessageService;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public CommonResponse<SendMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        SendMessageResponse response = messageService.sendMessage(request);
        return CommonResponse.onSuccess(response);
    }

    @GetMapping
    public CommonResponse<List<MessageResponse>> getMessages() {
        List<MessageResponse> messages = messageService.getMessages();
        return CommonResponse.onSuccess(messages);
    }

    @GetMapping("/{message_id}")
    public CommonResponse<MessageResponse> getMessage(@PathVariable("message_id") Long messageId) {
        MessageResponse response = messageService.getMessage(messageId);
        return CommonResponse.onSuccess(response);
    }

    @PatchMapping("/{message_id}")
    public CommonResponse<CancelMessageResponse> cancelScheduledMessage(@PathVariable("message_id") Long messageId) {
        CancelMessageResponse response = messageService.cancelScheduledMessage(messageId);
        return CommonResponse.onSuccess(response);
    }

    @DeleteMapping("/{message_id}")
    public CommonResponse<Void> deleteMessage(@PathVariable("message_id") Long messageId) {
        messageService.deleteMessage(messageId);
        return CommonResponse.onSuccess(null);
    }
}
