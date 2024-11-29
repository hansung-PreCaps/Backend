package com.pictalk.image.service;

import com.pictalk.global.component.OpenAIImageClient;
import com.pictalk.global.util.UrlImageDownloadUtil;
import com.pictalk.image.dto.ImageRequestDto;
import com.pictalk.image.dto.ImageResponseDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AiImageService {

    private static final String PROMPT_TEMPLATE = "이 프롬프트는 한국어로 작성되었습니다. \"%s\" 상황을 묘사한 이미지로, \"%s\" 분위기를 담아주세요. 이미지 스타일은 사실적이고, 고해상도로 표현해주세요.";

    private final OpenAIImageClient openAIImageClient;
    private final UrlImageDownloadUtil urlImageDownloadUtil;

    public ImageResponseDto.ImageResponse createAiImage(
            @RequestBody ImageRequestDto.CreateAiImageRequest imageRequest) throws IOException {
        String prompt = String.format(PROMPT_TEMPLATE,
                imageRequest.getSituation(),
                imageRequest.getAtmosphere());

        // Call the OpenAI API to generate the image
        String imageUrl = openAIImageClient.getImageUrlFromOpenAI(prompt);
        urlImageDownloadUtil.convertUrlToS3Url(imageUrl);

        return ImageResponseDto.ImageResponse.builder()
                .url(imageUrl)
                .build();
    }
}
