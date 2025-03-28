package com.markmycode.mmc.auth.jwt.filter;

import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.auth.service.TokenService;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.UnauthorizedException;
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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String accessToken = CookieUtils.getCookie(request, "Access_Token");

        // 토큰이 있으면 인증 처리
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                jwtTokenProvider.isExpired(accessToken);
                // 상태값 확인
                String userStatus = jwtTokenProvider.getClaim(accessToken, "userStatus");
                if ("INACTIVE".equals(userStatus)) {
                    throw new UnauthorizedException(ErrorCode.INACTIVE_USER);
                }
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UnauthorizedException e) {
                String refreshToken = CookieUtils.getCookie(request, "Refresh_Token");
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    try {
                        jwtTokenProvider.isExpired(refreshToken);
                        // 상태값 확인
                        String userStatus = jwtTokenProvider.getClaim(refreshToken, "userStatus");
                        if ("INACTIVE".equals(userStatus)) {
                            throw new UnauthorizedException(ErrorCode.INACTIVE_USER);
                        }
                        TokenResponseDto newToken = tokenService.refreshAccessToken(refreshToken);
                        accessToken = newToken.getAccessToken();
                        CookieUtils.addCookie(response, "Access_Token", accessToken, 30 * 60);
                        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("✅ Access Token 갱신: " + requestURI);
                    } catch (UnauthorizedException ex) {
                        System.out.println("❌ Refresh Token 만료: " + requestURI);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

// 로그아웃 시 블랙리스트 추가
//public void logout(HttpServletResponse response, String accessToken, String refreshToken) {
//    redisTemplate.opsForValue().set(accessToken, "blacklisted", 30, TimeUnit.MINUTES); // 액세스 토큰 유효 기간
//    redisTemplate.opsForValue().set(refreshToken, "blacklisted", 7, TimeUnit.DAYS); // 리프레시 토큰 유효 기간
//    CookieUtils.deleteCookie(response, "Access_Token");
//    CookieUtils.deleteCookie(response, "Refresh_Token");
//}