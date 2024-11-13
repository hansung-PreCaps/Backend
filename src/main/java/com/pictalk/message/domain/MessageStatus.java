package com.pictalk.message.domain;

public enum MessageStatus {
    SCHEDULED, // 예약됨
    SENT, // 전송됨
    CANCELLED, // 예약 취소됨
    TEMP // 임시 저장됨
}
