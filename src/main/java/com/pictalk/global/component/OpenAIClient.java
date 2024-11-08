package com.pictalk.global.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
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


// Global OpenAI API Client Component
@Component
public class OpenAIClient {

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final CloseableHttpClient httpClient;
    private final ObjectMapper mapper;

    private static final int TIMEOUT = 30000;

    public OpenAIClient(ObjectMapper mapper) {
        this.mapper = mapper;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public String getResponseFromOpenAI(String prompt) {
        HttpPost httpPost = new HttpPost(openAiApiUrl + "/chat/completions");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + openAiApiKey);

        try {
            StringEntity entity = new StringEntity(createRequestBody(prompt), "UTF-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != 200) {
                throw new GeneralException(ErrorStatus.OPENAI_SERVER_ERROR);
            }

            // Extract the content from the response JSON
            return extractContent(responseBody);

        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.OPENAI_SERVER_ERROR);
        }
    }

    private String createRequestBody(String prompt) throws JsonProcessingException {
        ObjectNode json = mapper.createObjectNode();
        json.put("model", "gpt-4o-mini");

        ArrayNode messages = mapper.createArrayNode();
        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        json.set("messages", messages);

        json.put("max_tokens", 1000);
        json.put("temperature", 0.8);

        return mapper.writeValueAsString(json);
    }

    private String extractContent(String responseBody) throws JsonProcessingException {
        // Parse the JSON response and retrieve the "content" field from the message
        JsonNode root = mapper.readTree(responseBody);
        JsonNode messageContent = root.path("choices").get(0).path("message").path("content");

        // Check if the content node exists and return its text value
        if (!messageContent.isMissingNode()) {
            return messageContent.asText();
        } else {
            throw new GeneralException(ErrorStatus.OPENAI_RESPONSE_NOT_FOUND);
        }
    }


}