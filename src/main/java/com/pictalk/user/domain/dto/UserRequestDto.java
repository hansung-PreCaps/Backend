package com.pictalk.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDto {

//    @Builder
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
