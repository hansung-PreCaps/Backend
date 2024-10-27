package com.pictalk.global.jwt;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.user.domain.User;
import com.pictalk.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

        // Access Token 추출 및 유효성 검사
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .flatMap(jwtService::extractEmail) // 이메일 추출
                .flatMap(userRepository::findByEmail) // 사용자 정보 조회
                .ifPresent(user -> {
                    log.info("사용자 정보가 발견되었습니다: {}", user);
                    saveAuthentication((User) user);
                    log.info("사용자 인증 정보가 저장되었습니다: {}", user);
                });

        // 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(User user) {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_VALID);
        }

        if (!password.matches(".*[a-z].*")) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_VALID);
        }

        if (!password.matches(".*\\d.*")) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_VALID);
        }

        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new GeneralException(ErrorStatus.USER_PASSWORD_NOT_VALID);
        }
    }
}
