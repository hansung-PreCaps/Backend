package com.pictalk.global.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class PpurioClient {
    @Value("${ppurio.api.url}")
    private String apiUrl;

    @Value("${ppurio.api.key}")
    private String apiKey;

    @Value("${ppurio.api.account}")
    private String ppurioAccount;

    @Value("${ppurio.api.refKey}")
    private String refKey;


}
