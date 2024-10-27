package com.pictalk.user.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.jwt.JwtRequestFilter;
import com.pictalk.global.jwt.JwtService;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.user.converter.UserConverter;
import com.pictalk.user.repository.UserRepository;
import com.pictalk.user.domain.dto.UserRequestDto.*;
import com.pictalk.user.domain.dto.UserResponseDto.LoginResponse;
import com.pictalk.user.domain.dto.UserResponseDto.UserResponse;
import com.pictalk.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtRequestFilter jwtRequestFilter;

    // 회원가입
    @Transactional
    public UserResponse registerUser(CreateUser createUser) {
//        if (userRepository.findByUsername(createUser.getUsername()).isPresent()) {
//            throw new GeneralException(ErrorStatus.USER_USERNAME_ALREADY_EXISTS);
//        }
        if (userRepository.existsByUsername(createUser.getUsername())) {
            throw new GeneralException(ErrorStatus.USER_USERNAME_ALREADY_EXISTS);
        }
        if(userRepository.existsByEmail(createUser.getEmail())) {
            throw new GeneralException(ErrorStatus.USER_EMAIL_ALREADY_EXISTS);
        }

        jwtRequestFilter.validatePassword(createUser.getPassword());
        User user = UserConverter.toEntity(createUser, passwordEncoder);
        return UserConverter.toResponse(userRepository.save(user));
    }

    // 로그인
    public String login(LoginUser loginUser) {
        User user = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtService.createAccessToken(user.getUsername());
        String refreshToken = jwtService.createRefreshToken();

        // 리프레시 토큰 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return accessToken;
    }

    // 로그아웃
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
    }

    // 리프레시 토큰을 이용한 액세스 토큰 재발급
    public LoginResponse refreshAccessToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtService.extractEmail(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh token does not match");
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtService.createAccessToken(user.getUsername());

        // 새로운 리프레시 토큰도 생성하고 저장
        String newRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);

        // 클라이언트에는 새 액세스 토큰만 반환
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
