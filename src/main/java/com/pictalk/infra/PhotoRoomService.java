package com.pictalk.infra;

import static java.rmi.server.LogStream.log;

import com.pictalk.global.config.PhotoRoomConfig;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
//            String encodedImageUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8);
            String url = UriComponentsBuilder.fromHttpUrl(photoRoomConfig.getPhotoRoomUrl())
                    .queryParam("referenceBox=originalImage")
                    .queryParam("removeBackground", false)
                    .queryParam("textRemoval.mode", "ai.all")
                    .queryParam("imageUrl", imageUrl)
                    .toUriString();

            // 헤더 설정
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
//                            "https://image-api.photoroom.com/v2/edit?imageUrl=https%3A%2F%2Fstore-images.s-microsoft.com%2Fimage%2Fapps.28472.2bebd0ae-9ba9-4e5b-8ed1-30e21611ff72.b631c904-5b3d-40ef-8af5-26a74cebba4d.8d582b7a-e6d0-4d9c-8a28-037cdefc5568&referenceBox=originalImage&removeBackground=false&textRemoval.mode=ai.all"))
                    .header("Accept", "image/png, application/json")
                    .header("x-api-key", "sandbox_8b3c3d161f793b47cc3ebdedde04dcd4823cb972")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            log("PhotoRoom API call: " + url);

            HttpClient client = HttpClient.newHttpClient();

            // Send the request and gt the response as a byte array
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // Convert the response body to a BufferedImage
            byte[] imageBytes = response.body();

            return imageBytes;

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.PHOTOROOM_API_ERROR);
        }
    }

}
