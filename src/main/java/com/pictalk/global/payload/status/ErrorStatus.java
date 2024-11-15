package com.pictalk.global.payload.status;

import com.pictalk.global.payload.response.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {
    // Common Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러입니다. 관리자에게 문의하세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),

    // User Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409", "이미 존재하는 사용자입니다."),
    USER_USERNAME_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_NAME_400", "사용자 이름이 일치하지 않습니다."),
    USER_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_PW_400", "비밀번호가 일치하지 않습니다."),
    USER_EMAIL_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_EM_400", "이메일이 일치하지 않습니다."),
    USER_USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_NAME_409", "이미 존재하는 사용자 이름입니다."),
    USER_USERNAME_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_NAME_400", "사용자 이름이 유효하지 않습니다."),

    PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "PW_400", "비밀번호는 최소 8자 이상이어야 하며, 하나 이상의 숫자, 특수 문자를 포함해야 합니다."),

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EM_409", "이미 존재하는 이메일입니다."),
    EMAIL_NOT_VALID(HttpStatus.BAD_REQUEST, "EM_400", "이메일이 유효하지 않습니다."),

    REFRESH_TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_RT_400", "리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "USER_RT_401", "리프레시 토큰이 만료되었습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "USER_AT_401", "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_400", "액세스 토큰이 유효하지 않습니다."),

    OPENAI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OpenAI_500", "openai server error"),
    OPENAI_RESPONSE_NOT_FOUND(HttpStatus.BAD_GATEWAY, "OPENAI_502", "OpenAI response no content"),

    // Message Error
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_2001", "메시지를 찾을 수 없습니다."),
    FILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500", "파일 변환에 실패했습니다."),
    NOT_SUPPORTED_IMAGE_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_500", "지원하지 않는 이미지 타입입니다."),
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_404", "해당 그룹을 찾을 수 없습니다"),
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "RECEIVER_404", "해당 수신자를 찾을 수 없습니다"),
    GROUP_RECEIVER_ALREADY_EXISTS(HttpStatus.CONFLICT, "GROUP_409", "그룹에 해당 수신자가 이미 존재합니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .status(httpStatus)
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }
}
