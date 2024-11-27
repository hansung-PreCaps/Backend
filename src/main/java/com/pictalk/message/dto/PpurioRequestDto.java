package com.pictalk.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pictalk.message.dto.MessageRequestDto.FileDto;
import com.pictalk.message.dto.MessageRequestDto.Resend;
import com.pictalk.message.dto.MessageRequestDto.Target;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpurioRequestDto {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PpurioMMSRequestDto {
        private String account;
        private String messageType;
        private String content;
        private String duplicateFlag = "N";
        private int targetCount;
        private List<Target> targets;
        private String sendTime;
        private String refKey;           // 고객사 부여 키
        private String rejectType;       // 수신 거부 설정
        private String from;             // 발신 번호// 중복 플래그 (Y/N)
        private String subject;          // 제목
        private FileDto[] files;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PpurioKakaoRequestDto {
        private String account;
        private String messageType;
        private String content;
        private String duplicateFlag = "N";
        private int targetCount;
        private List<Target> targets;
        private String sendTime;
        private String refKey;           // 고객사 부여 키
        private String rejectType;       // 수신 거부 설정
        private String templateCode;     // 템플릿 코드
        private String senderProfile;    // 발신 프로필
        private String isResend;         // 재전송 여부
        private Resend resend;

    }

    @Getter
    @Builder
    public static class PpurioCancelRequestDto {
        private String account;
        private String messageKey;
    }
}
