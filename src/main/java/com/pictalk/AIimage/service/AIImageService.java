package com.pictalk.AIimage.service;

import com.pictalk.AIimage.domain.dto.ImageRequestDto;
import com.pictalk.AIimage.domain.dto.ImageResponseDto;
import com.pictalk.global.component.OpenAIImageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AIImageService {

    private static final String promptTemplate = "Create a high-quality, detailed image depicting the following situation: \\\"%s\\\". The image should evoke the atmosphere or feeling of \\\"%s\\\".";

    private final OpenAIImageClient openAIImageClient;

    public ImageResponseDto.ImageResponse createAIImage(@RequestBody ImageRequestDto.CreateAIImageRequest imageRequest) {
        String prompt = String.format(promptTemplate,
                imageRequest.getSituation(),
                imageRequest.getAtmosphere());

        // Call the OpenAI API to generate the image
        String imageUrl = openAIImageClient.getImageUrlFromOpenAI(prompt);

        ImageResponseDto.ImageResponse imageResponse = ImageResponseDto.ImageResponse.builder()
                .url(imageUrl)
                .build();

        return imageResponse;
    }
}
