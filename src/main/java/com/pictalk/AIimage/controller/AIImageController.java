package com.pictalk.AIimage.controller;


import com.pictalk.AIimage.dto.ImageRequestDto;
import com.pictalk.AIimage.dto.ImageResponseDto;
import com.pictalk.AIimage.service.AIImageService;
import com.pictalk.global.payload.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai_images")
public class AIImageController {

    private final AIImageService aiImageService;

    @Operation(summary = "Create an AI-generated image")
    @PostMapping
    public CommonResponse<ImageResponseDto.ImageResponse> createAIImage(@Valid @RequestBody ImageRequestDto.CreateAIImageRequest imageRequest) {
        ImageResponseDto.ImageResponse imageResponse = aiImageService.createAIImage(imageRequest);
        return CommonResponse.onSuccess(imageResponse);
    }

}
