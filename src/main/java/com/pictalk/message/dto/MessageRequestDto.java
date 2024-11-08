package com.pictalk.message.dto;

import java.util.List;
import lombok.Getter;

public class MessageRequestDto {
    @Getter
    public static class CreateAIMessageRequest {
        private String situation;
        private List<String> keyword;
    }
}
