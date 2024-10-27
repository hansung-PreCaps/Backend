package com.pictalk.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserResponseDto {

    @Getter
    @Builder
    public static class UserResponse {
        private String username;
        private String email;
    }

    @Getter
    @Builder
    public static class LoginResponse {
        private String accessToken;
    }
}
