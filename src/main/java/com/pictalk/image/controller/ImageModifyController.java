package com.pictalk.image.controller;

import com.pictalk.infra.PhotoRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageModifyController {
    private final PhotoRoomService photoroomService;

    @GetMapping("/edit")
    public ResponseEntity<byte[]> editImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            byte[] editedImage = photoroomService.editImageWithUrl(imageUrl);

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
