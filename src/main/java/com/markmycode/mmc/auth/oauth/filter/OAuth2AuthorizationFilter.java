package com.markmycode.mmc.auth.oauth.filter;

import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.auth.oauth.service.TokenService;
import com.markmycode.mmc.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 소셜 사용자 JWT 인가
@Component
@RequiredArgsConstructor
public class OAuth2AuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService; // 직접 주입하지 않고 ApplicationContext에서 가져옴

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 쿠키에서 Access Token 추출
        String accessToken = CookieUtils.getCookie(request, "Access_Token");

        // 토큰 존재 여부 확인
        if (accessToken == null || accessToken.isEmpty()) {
            System.out.println("Access_Token is Null");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token is missing");
            return;
        }

        // 토큰 만료 여부 확인 및 리프레시
        if (jwtTokenProvider.isExpired(accessToken)) {
            System.out.println("Access_Token is Expired");
            String refreshToken = CookieUtils.getCookie(request, "Refresh_Token");
            if (refreshToken != null && !jwtTokenProvider.isExpired(refreshToken)) {
                TokenResponseDto newToken = tokenService.refreshAccessToken(refreshToken);
                // 쿠키에 새 토큰 설정
                CookieUtils.addCookie(response, "Access_Token", newToken.getAccessToken());
                CookieUtils.addCookie(response, "Refresh_Token", newToken.getRefreshToken());
                accessToken = newToken.getAccessToken();
            } else {
                // 리프레시 토큰도 만료된 경우
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }
        }

        // 인증 정보 설정
        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}
