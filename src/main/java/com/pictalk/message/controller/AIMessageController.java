package com.pictalk.message.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.message.dto.MessageRequestDto;
import com.pictalk.message.dto.MessageResponseDto;
import com.pictalk.message.service.AIMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai_messages")
public class AIMessageController {

    private final AIMessageService aiMessageService;

    @PostMapping
    public CommonResponse<MessageResponseDto.CreateAIMessageResponse> createMessage(@RequestBody MessageRequestDto.CreateAIMessageRequest message) {
        MessageResponseDto.CreateAIMessageResponse createAIMessageResponse = aiMessageService.generateMessage(message.getSituation());
        return CommonResponse.onSuccess(createAIMessageResponse);

    }
}
