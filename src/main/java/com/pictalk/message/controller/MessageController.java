package com.pictalk.message.controller;

import com.pictalk.message.dto.MessageRequestDto;
import com.pictalk.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    // 메시지 발송
    @PostMapping
    public void sendMessage(@RequestBody MessageRequestDto.CommonMessage commonMessage) {
        // 메시지 전송 로직
        messageService.requestSend(commonMessage);
    }

//    @PostMapping
//    public void kkaoSendMessage(@RequestBody MessageRequestDto.kkaoMessage kkaoMessage) {
//        // 카카오 메시지 전송 로직
//        messageService.requestSend(kkaoMessage);
//    }

}
