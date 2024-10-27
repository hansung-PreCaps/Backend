package com.pictalk.user;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserConverter {

    public static User toEntity(UserRequestDto.CreateUser createUser, PasswordEncoder passwordEncoder) {

        return User.builder()
                .username(createUser.getUsername())
                .password(passwordEncoder.encode(createUser.getPassword()))
                .email(createUser.getEmail())
                .build();
    }

    public static UserResponseDto.UserResponse toResponse(User user) {
        return UserResponseDto.UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

}
