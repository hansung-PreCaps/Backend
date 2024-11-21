package com.pictalk.image.dto;

import lombok.Getter;

public class ImageRequestDto {

    @Getter
    public static class CreateAiImageRequest {
        private String situation;
        private String atmosphere;
    }

    @Getter
    public static class EditImageRequest {
        private String imageUrl;
    }
}
