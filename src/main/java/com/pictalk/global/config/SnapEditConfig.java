package com.pictalk.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SnapEditConfig {

    @Value("${snapedit.api.key}")
    private String apiKey;

    @Value("${snapedit.api.object-removal-url}")
    private String objectRemovalUrl;

    @Value("${snapedit.api.background-removal-url}")
    private String backgroundRemovalUrl;
}