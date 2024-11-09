package com.pictalk.aiimage.dto;

import lombok.Builder;
import lombok.Getter;

public class ImageResponseDto {

    @Getter
    @Builder
    public static class ImageResponse {
        private String url;
    }
}
