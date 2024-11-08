package com.pictalk.message.dto;

import lombok.Builder;
import lombok.Getter;

public class MessageResponseDto {
    @Getter
    @Builder
    public static class CreateAIMessageResponse {
        private String message;
    }

}
