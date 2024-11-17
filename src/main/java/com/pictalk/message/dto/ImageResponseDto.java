package com.pictalk.message.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class ImageResponseDto {

    @Getter
    @Builder
    public static class GetImagesResponse {
        private List<ImageResponse> images;
    }

    @Getter
    @Builder
    public static class ImageResponse {
        private Long imageId;
        private String imageUrl;
        private String imageName;
    }
}
