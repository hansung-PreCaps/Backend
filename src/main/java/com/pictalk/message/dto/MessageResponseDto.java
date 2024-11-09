package com.pictalk.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MessageResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageResponse {
        private String externalMessageId;
        private String status;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageResponse {
        private Long messageId;
        private String content;
        private String to;
        private String sendTime;
        private String status;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelMessageResponse {
        private Long messageId;
        private String status;
    }

    @Getter
    @Builder
    public static class CreateAIMessageResponse {
        private String message;
    }
}
