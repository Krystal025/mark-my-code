package com.markmycode.mmc.auth.jwt.filter;

import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.auth.oauth.service.TokenService;
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
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("✅ SecurityContext 설정: " + requestURI);
            } catch (UnauthorizedException e) {
                String refreshToken = CookieUtils.getCookie(request, "Refresh_Token");
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    try {
                        jwtTokenProvider.isExpired(refreshToken);
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
