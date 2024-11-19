package com.pictalk.infra;

import com.pictalk.global.config.SnapEditConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SnapEditService {

    private final SnapEditConfig snapEditConfig;
    private final RestTemplate restTemplate;


    public String removeText(MultipartFile image, MultipartFile mask) {
        String url = snapEditConfig.getObjectRemovalUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-API-KEY", snapEditConfig.getApiKey());

        Map<String, Object> body = new HashMap<>();
        body.put("original_preview_image", image.getResource());
        body.put("mask_brush", mask.getResource());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    public String removeBackground(MultipartFile image) {
        String url = snapEditConfig.getBackgroundRemovalUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-API-KEY", snapEditConfig.getApiKey());

        Map<String, Object> body = new HashMap<>();
        body.put("input_image", image.getResource());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, request, String.class);
    }
}