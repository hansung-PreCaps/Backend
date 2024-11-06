package com.pictalk.user.controller;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.user.dto.UserRequestDto;
import com.pictalk.user.dto.UserResponseDto.LoginResponse;
import com.pictalk.user.dto.UserResponseDto.UserResponse;
import com.pictalk.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public CommonResponse<UserResponse> signup(@Valid @RequestBody UserRequestDto.CreateUser createUser) {
        UserResponse userResponse = userService.registerUser(createUser);
        return CommonResponse.onSuccess(userResponse);
    }

    @PostMapping("/signin")
    public CommonResponse<LoginResponse> login(@Valid @RequestBody UserRequestDto.LoginUser loginUser) {
        LoginResponse loginResponse = userService.login(loginUser);
        return CommonResponse.onSuccess(loginResponse);

    }

    // 로그아웃 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // Authorization 헤더에서 Access Token 추출 후 로그아웃 처리
        userService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public CommonResponse<String> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        if (!refreshToken.startsWith("Bearer ")) {
            throw new GeneralException(ErrorStatus.USER_REFRESH_TOKEN_NOT_VALID);
        }
        String token = refreshToken.substring(7);
        String newAccessToken = userService.refreshAccessToken(token);
        return CommonResponse.onSuccess(newAccessToken);
    }
}
