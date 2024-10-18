package com.pictalk.global.exception;

import com.pictalk.global.payload.response.ReasonDto;
import com.pictalk.global.payload.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseStatus status;

    public ReasonDto getErrorReason() {
        return this.status.getReason();
    }

    public ReasonDto getErrorReasonHttpStatus() {
        return this.status.getReasonHttpStatus();
    }
}
