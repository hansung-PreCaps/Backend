package com.pictalk.user.converter;

import com.pictalk.user.domain.User;
import com.pictalk.user.dto.UserRequestDto;
import com.pictalk.user.dto.UserResponseDto;
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

    public static UserResponseDto.LoginResponse toLoginResponse(String accessToken, String refreshToken) {
        return UserResponseDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
