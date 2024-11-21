package com.pictalk.global.payload.status;

import com.pictalk.global.payload.response.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseStatus {
    // Common Success
    OK(HttpStatus.OK, "COMMON_200", "성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_201", "성공적으로 생성되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON_204", "성공적으로 삭제되었습니다."),

    UPLOAD_IMAGE_SUCCESS(HttpStatus.CREATED, "IMAGE_201", "이미지가 성공적으로 저장되었습니다"),
    GET_IMAGE_SUCCESS(HttpStatus.OK, "IMAGE_200", "이미지가 성공적으로 조회되었습니다"),

    GROUP_CREATED(HttpStatus.CREATED, "GROUP_201", "그룹이 성공적으로 생성되었습니다"),
    GROUP_DELETED(HttpStatus.NO_CONTENT, "GROUP_204", "그룹이 성공적으로 삭제되었습니다"),
    GROUP_UPDATED(HttpStatus.OK, "GROUP_200", "그룹 이름 변경이 성공적으로 처리되었습니다."),
    GROUP_RECEIVER_ADDED(HttpStatus.CREATED, "GR_RC_201", "그룹 멤버 추가가 성공적으로 처리되었습니다."),
    GROUP_RECEIVERS_FOUND(HttpStatus.OK, "GR_RC_200", "그룹 멤버가 성공적으로 조회되었습니다."),
    GROUP_RECEIVER_DELETED(HttpStatus.NO_CONTENT, "GR_RC_204", "그룹 멤버 삭제가 성공적으로 처리되었습니다."),
    REMOVE_TEXT_SUCCESS(HttpStatus.OK, "RT_200", "이미지 내 텍스트가 성공적으로 삭제되었습니다"),
    REMOVE_BACKGROUND_SUCCESS(HttpStatus.OK, "RB_200", "이미지 내 배경이 성공적으로 삭제되었습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .status(httpStatus)
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }
}
