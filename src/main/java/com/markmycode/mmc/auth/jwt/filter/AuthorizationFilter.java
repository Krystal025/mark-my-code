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
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    private boolean isPublicPath(String uri, String method) {
        return uri.equals("/") // 메인 페이지 경로를 공용 경로로 허용
                || uri.startsWith("/login")
                || uri.startsWith("/auth/")
                || uri.startsWith("/oauth2/authorization")
                || uri.startsWith("/oauth2/callback")
                || uri.startsWith("/users/signup")
                || (uri.startsWith("/posts") && "GET".equals(method))
                || (uri.startsWith("/posts/") && "GET".equals(method))
                || (uri.startsWith("/comments/") && "GET".equals(method));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String accessToken = CookieUtils.getCookie(request, "Access_Token");

        // 1. Access Token 확인 및 SecurityContext 설정
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
        //filterChain.doFilter(request, response);

        // 2. public 경로면 바로 통과
        if (isPublicPath(requestURI, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 비공개 경로면 토큰 필수
        if (accessToken == null || accessToken.isEmpty()) {
            response.sendRedirect("/auth/login");
            return;
        }
        try {
            jwtTokenProvider.isExpired(accessToken);
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            response.sendRedirect("/auth/login");
        }
    }

}
