package com.pictalk.global.payload.status;

import com.pictalk.global.payload.ReasonDto;

public interface BaseStatus {
    public ReasonDto getReason();

    public ReasonDto getReasonHttpStatus();
}
