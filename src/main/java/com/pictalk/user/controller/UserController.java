package com.pictalk.user.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.user.domain.dto.UserRequestDto;
import com.pictalk.user.domain.dto.UserResponseDto.LoginResponse;
import com.pictalk.user.domain.dto.UserResponseDto.UserResponse;

import com.pictalk.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<UserResponse>> signup(@Valid @RequestBody UserRequestDto.CreateUser createUser) throws IOException {
        UserResponse userResponse = userService.registerUser(createUser);
        CommonResponse<UserResponse> response = CommonResponse.onSuccess(userResponse);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<CommonResponse<String>> login(@Valid @RequestBody UserRequestDto.LoginUser loginUser) throws IOException{
        String accessToken =  userService.login(loginUser);
        return ResponseEntity.status(200).body(CommonResponse.onSuccess(accessToken));

    }
    // 로그아웃 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // Authorization 헤더에서 Access Token 추출 후 로그아웃 처리
        userService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<LoginResponse>> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) throws IOException {
        if (!refreshToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        String token = refreshToken.substring(7);
        LoginResponse loginResponse = userService.refreshAccessToken(token);
        CommonResponse<LoginResponse> response = CommonResponse.onSuccess(loginResponse);
        return ResponseEntity.status(200).body(response);
    }
}
