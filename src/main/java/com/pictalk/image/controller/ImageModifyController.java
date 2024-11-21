package com.pictalk.image.controller;

import com.pictalk.image.dto.ImageRequestDto.EditImageRequest;
import com.pictalk.infra.PhotoRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageModifyController {
    private final PhotoRoomService photoroomService;

    @PatchMapping("/edit")
    public ResponseEntity<byte[]> editImage(@RequestBody EditImageRequest editImageRequest) {
        try {
            byte[] editedImage = photoroomService.editImageWithUrl(editImageRequest.getImageUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
//            headers.setContentLength(editedImage.length);

            return new ResponseEntity<>(editedImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
