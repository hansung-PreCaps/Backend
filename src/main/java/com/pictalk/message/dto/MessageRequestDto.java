package com.pictalk.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MessageRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest {
        private String account;
        private String messageType;
        private String content;
        private String from;
        private String duplicateFlag;
        private int targetCount;
        private List<Target> targets;
        private String refKey;
        private String rejectType;
        private String sendTime;
        private String subject;
        private List<FileDto> files;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Target {
        private String to;
        private ChangeWord changeWord;
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
        private int size;
        private String data;
    }
}
