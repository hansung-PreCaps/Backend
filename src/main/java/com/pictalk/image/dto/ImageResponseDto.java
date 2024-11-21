package com.pictalk.image.dto;

import lombok.Builder;
import lombok.Getter;

public class ImageResponseDto {

    @Getter
    @Builder
    public static class ImageResponse {
        private String url;
    }
}
