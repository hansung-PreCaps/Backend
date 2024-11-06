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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_1001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_1002", "이미 존재하는 사용자입니다."),
    USER_USERNAME_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_1003", "사용자 이름이 일치하지 않습니다."),
    USER_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_1004", "비밀번호가 일치하지 않습니다."),
    USER_EMAIL_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_1005", "이메일이 일치하지 않습니다."),

    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_1006", "이미 존재하는 이메일입니다."),
    USER_USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_1007", "이미 존재하는 사용자 이름입니다."),
    USER_PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_1008", "비밀번호는 최소 8자 이상이어야 하며, 하나 이상의 숫자, 특수 문자를 포함해야 합니다."),
    USER_EMAIL_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_1009", "이메일이 유효하지 않습니다."),
    USER_USERNAME_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_1010", "사용자 이름이 유효하지 않습니다."),
    USER_REFRESH_TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_1011", "리프레시 토큰이 유효하지 않습니다."),
    USER_REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "USER_1012", "리프레시 토큰이 만료되었습니다."),
    USER_ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "USER_1013", "액세스 토큰이 만료되었습니다."),
    USER_ACCESS_TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, "USER_1014", "액세스 토큰이 유효하지 않습니다."),

    // Message Error
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_2001", "메시지를 찾을 수 없습니다."),
    ;

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
