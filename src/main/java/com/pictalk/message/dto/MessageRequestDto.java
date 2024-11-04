package com.pictalk.message.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MessageRequestDto {

    @Getter
    @AllArgsConstructor
    public static class CommonMessage {
        private String messageType;
        private String content;
        private String from;
        private String duplicateFlag;
        private int targetCount;
        private List<TargetDto> targets;
        private String refKey;
        private String rejectType;
        private String sendTime;
        private String subject;
        private List<FileDto> files;

    }


    public static class TargetDto {
        private String to;
        private Map<String, String> changeWord;
        private String name;
    }

    public static class FileDto {
        private String name;
        private long size;
        private String data;

    }

    @Getter
    @AllArgsConstructor
    public static class kkaoMessage {
        private String account;
        private String messageType;
        private String senderProfile;
        private String templateCode;
        private String content;
        private Long targetCount;
        private List<String> targets;
        private String senderNumber;
    }

}
