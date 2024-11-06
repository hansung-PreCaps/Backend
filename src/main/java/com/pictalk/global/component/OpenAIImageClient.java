package com.pictalk.global.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAIImageClient {

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final CloseableHttpClient httpClient;
    private static final int TIMEOUT = 30000;

    public OpenAIImageClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public String getImageUrlFromOpenAI(String prompt) {
        String apiUrl = openAiApiUrl + "/images/generations";
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + openAiApiKey);

        try {
            StringEntity entity = new StringEntity(createRequestBody(prompt), "UTF-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != 200) {
                // Handle error
                throw new RuntimeException("Failed to call OpenAI API: " + responseBody);
            }

            // Parse the response to extract the image URL
            return parseImageUrl(responseBody);

        } catch (IOException e) {
            throw new RuntimeException("Failed to call OpenAI API", e);
        }
    }

    private String createRequestBody(String prompt) throws JsonProcessingException {
        // Use Jackson to create JSON request body
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.createObjectNode()
                .put("prompt", prompt)
                .put("n", 1) // Number of images to generate
                .put("size", "1024x1024"); // Image size

        return mapper.writeValueAsString(json);
    }

    private String parseImageUrl(String responseBody) throws JsonProcessingException {
        // Parse the JSON response to get the image URL
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);

        // The response structure:
        // { "created": 1234567890, "data": [{ "url": "..." }] }
        JsonNode dataNode = root.path("data");
        if (dataNode.isArray() && dataNode.size() > 0) {
            JsonNode firstItem = dataNode.get(0);
            String imageUrl = firstItem.path("url").asText();
            return imageUrl;
        } else {
            throw new RuntimeException("No image URL found in OpenAI API response.");
        }
    }
}
