package com.pictalk.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pictalk.message.domain.MessageStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MessageRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest extends BaseMessageDto {
        private String content;
        private String from;
        private String subject;
        private String rejectType = "AD";
    }

    @Getter
    public static class SendKakaoRequest extends BaseMessageDto {
        private String senderProfile;
        private String templateCode;
        private String isResend = "Y";
        private List<ResendDto> resends;
    }

    @Getter
    public static class BaseMessageDto {
        private MessageStatus status = MessageStatus.SENT;
        private String duplicateFlag = "N";
        private int targetCount;
        private List<Target> targets;
        private String sendTime;
        private String messageType;
    }

    @Getter
    public static class ResendDto {
        private String messageType = "ALT";
        private String content;
        private String from;
        private String subject;

    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Resend {
        private String messageType;
        private String content;
        private String from;
        private String subject;
        private FileDto[] files;

        // Getter and Setter

        @Getter
        @Builder
        public static class ResendFile {
            private String fileKey;
            private String name;
            private String fileType;
            private String fileUrl;
            // Getter and Setter
        }
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Target {
        private String to;
        private Map<String,String> changeWord;
        private String name;
    }

    @Getter
    public static class ChangeWord {
        private String var1;
        private String var2;
        private String var3;
        private String var4;
        private String var5;
        private String var6;
        private String var7;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileDto {
        private String name;
        private Long size;
        private String data;
    }

    @Getter
    public static class CreateAIMessageRequest {
        private String situation;
        private List<String> keyword;
    }
}
