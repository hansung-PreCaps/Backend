package com.pictalk.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PpurioConfig {

    @Value("${ppurio.api.url}")
    private String apiUrl;

    @Value("${ppurio.api.key}")
    private String apiKey;

    @Value("${ppurio.api.account}")
    private String ppurioAccount;
}
