package com.pictalk.global.component;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private static final int TIMEOUT = 30000;

    public OpenAIClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public String getResponseFromOpenAI(String prompt) {
        HttpPost httpPost = new HttpPost(openAiApiUrl);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + openAiApiKey);

        try {
            StringEntity entity = new StringEntity(createRequestBody(prompt), "UTF-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                throw new GeneralException(ErrorStatus.OPENAI_SERVER_ERROR);
            }
            return EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            throw new RuntimeException("OpenAI API 호출 실패", e);
        }
    }

    private String createRequestBody(String prompt) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("model", "gpt-3.5-turbo");

        ArrayNode messages = mapper.createArrayNode();
        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        json.set("messages", messages);

        json.put("max_tokens", 1000);
        json.put("temperature", 1);

        return mapper.writeValueAsString(json);
    }


}