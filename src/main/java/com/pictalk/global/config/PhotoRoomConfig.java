package com.pictalk.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PhotoRoomConfig {

    @Value("${photoroom.api.url}")
    private String photoRoomUrl;

    @Value("${photoroom.api.key}")
    private String photoRoomApiKey;

    @Value("${sandbox.api.key}")
    private String sandboxApiKey;
}
