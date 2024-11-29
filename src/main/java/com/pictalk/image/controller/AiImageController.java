package com.pictalk.image.controller;


import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.global.payload.status.SuccessStatus;
import com.pictalk.image.dto.ImageRequestDto;
import com.pictalk.image.dto.ImageResponseDto;
import com.pictalk.image.service.AiImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class AiImageController {

    private final AiImageService aiImageService;

    @Operation(summary = "Create an AI-generated image")
    @PostMapping
    public CommonResponse<ImageResponseDto.ImageResponse> createAIImage(
            @Valid @RequestBody ImageRequestDto.CreateAiImageRequest imageRequest) throws IOException {
        ImageResponseDto.ImageResponse imageResponse = aiImageService.createAiImage(imageRequest);
        return CommonResponse.of(SuccessStatus.CREATED, imageResponse);
    }

}
