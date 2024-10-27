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
    @NoArgsConstructor
    public static class LoginUser {
        private String username;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateUser {
        private String username;
        private String password;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdatePassword {
        private String currentPassword;
        private String newPassword;
    }

    @Getter
    public class LogoutRequest {
        private String username;
    }

}
