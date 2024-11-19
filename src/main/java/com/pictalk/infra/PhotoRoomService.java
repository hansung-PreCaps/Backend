package com.pictalk.infra;

import static java.rmi.server.LogStream.log;

import com.pictalk.global.config.PhotoRoomConfig;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoRoomService {

    private final PhotoRoomConfig photoRoomConfig;
    private final RestTemplate restTemplate;

    public byte[] editImageWithUrl(String imageUrl) {

        try {
            // URL 생성
            String encodedImageUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8);
            String url = UriComponentsBuilder.fromHttpUrl(photoRoomConfig.getPhotoRoomUrl())
                    .queryParam("referenceBox=originalImage")
                    .queryParam("removeBackground", false)
                    .queryParam("textRemoval.mode", "ai.all")
                    .queryParam("imageUrl", encodedImageUrl)
                    .toUriString();

            // 헤더 설정
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "image/png, application/json")
                    .header("x-api-key", photoRoomConfig.getSandboxApiKey())
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            log("PhotoRoom API call: " + url);

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            return response.body().getBytes();

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PHOTOROOM_API_ERROR);
        }
    }

}
