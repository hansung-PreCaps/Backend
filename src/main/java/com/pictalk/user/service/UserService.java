package com.pictalk.user.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.jwt.JwtRequestFilter;
import com.pictalk.global.jwt.JwtService;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.user.converter.UserConverter;
import com.pictalk.user.domain.User;
import com.pictalk.user.dto.UserRequestDto;
import com.pictalk.user.dto.UserResponseDto.LoginResponse;
import com.pictalk.user.dto.UserResponseDto.UserResponse;
import com.pictalk.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    public UserResponse registerUser(UserRequestDto.CreateUser createUser) {
        if (userRepository.existsByUsername(createUser.getUsername())) {
            throw new GeneralException(ErrorStatus.USER_USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(createUser.getEmail())) {
            throw new GeneralException(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }

        jwtRequestFilter.validatePassword(createUser.getPassword());
        User user = UserConverter.toEntity(createUser, passwordEncoder);
        return UserConverter.toResponse(userRepository.save(user));
    }

    // 로그인
    @Transactional
    public LoginResponse login(UserRequestDto.LoginUser loginUser) {
        User user = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        // 리프레시 토큰 저장
        user.updateRefreshToken(refreshToken);
        return UserConverter.toLoginResponse(accessToken, refreshToken);
    }

    // 로그아웃
    @Transactional
    public void logout(HttpServletRequest request) {
        // Access Token 추출 및 존재 여부 확인
        String accessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCESS_TOKEN_NOT_VALID));

        // Access Token에서 이메일 추출 후 사용자 조회
        String email = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCESS_TOKEN_NOT_VALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 리프레시 토큰 무효화
        user.updateRefreshToken(null);
    }

    // 리프레시 토큰을 이용한 액세스 토큰 재발급
    public String refreshAccessToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_VALID);
        }

        String email = jwtService.extractEmail(refreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_VALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_VALID);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtService.createAccessToken(user.getEmail());

        // 클라이언트에는 새 액세스 토큰만 반환
        return newAccessToken;
    }

    public LoginResponse refreshAllToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_VALID);
        }

        String email = jwtService.extractEmail(refreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_VALID));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_VALID);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtService.createAccessToken(user.getEmail());

        // 새로운 리프레시 토큰 생성
        String newRefreshToken = jwtService.reIssueRefreshToken(user);
        // 클라이언트에는 새 액세스 토큰만 반환
        return UserConverter.toLoginResponse(newAccessToken, newRefreshToken);
    }
}
