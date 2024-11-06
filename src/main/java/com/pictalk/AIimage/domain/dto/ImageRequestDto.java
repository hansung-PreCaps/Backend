package com.pictalk.AIimage.domain.dto;

import lombok.Getter;

public class ImageRequestDto {
    @Getter
    public static class CreateAIImageRequest {
        private String situation;
        private String atmosphere;
    }
}
