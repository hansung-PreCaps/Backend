package com.pictalk.message.dto;

import lombok.Getter;

public class MessageRequestDto {
    @Getter
    public static class CreateAIMessageRequest {
        private String situation;
    }

}
