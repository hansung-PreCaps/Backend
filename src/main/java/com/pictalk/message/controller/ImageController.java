package com.pictalk.message.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.global.payload.status.SuccessStatus;
import com.pictalk.message.service.MessageImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "image", description = "이미지 API")
@RequestMapping("/api/images")
public class ImageController {
    private final MessageImageService messageImageService;

    // 이미지 업로드
    @Operation(summary = "이미지 업로드")
    @PostMapping(value = "/messages/{message_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse> uploadImage(@PathVariable("message_id") Long messageId,
                                                      @RequestPart(name = "image_file")
                                                      @Valid MultipartFile imageFile) {
        messageImageService.createImage(messageId, imageFile);
        return ResponseEntity.ok(CommonResponse.of(SuccessStatus.UPLOAD_IMAGE_SUCCESS, null));
    }
}
