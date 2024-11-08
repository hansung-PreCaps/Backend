package com.pictalk.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserRequestDto {

    @Getter
    @AllArgsConstructor
    public static class CreateUser {
        private String username;
        private String password;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginUser {
        private String username;
        private String password;
    }
}
