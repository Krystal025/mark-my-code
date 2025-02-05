package com.markmycode.mmc.auth.jwt;

import com.markmycode.mmc.auth.service.TokenService;
import com.markmycode.mmc.auth.util.CookieUtils;
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
        // 1. Authorization 헤더에서 토큰 추출
        String accessToken = request.getHeader("Authorization");
        // 2. 토큰 존재 여부 및 형식 확인
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            System.out.println("Access_Token is Null");
            filterChain.doFilter(request, response);
            return;
        }
        // 3. "Bearer " 접두사 제거
        accessToken = accessToken.substring(7);
        // 4. 토큰 만료 여부 확인
        if(jwtTokenProvider.isExpired(accessToken)){
            System.out.println("Access_Token is Expired");
            // 5. Refresh Token으로 새로운 Access Token 발급 시도
            String refreshToken = CookieUtils.getCookie(request, "Refresh_Token");
            if(refreshToken != null && !jwtTokenProvider.isExpired(refreshToken)) {
                String newAccessToken = tokenService.refreshAccessToken(refreshToken).getAccessToken();
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                accessToken = newAccessToken; // 새로운 Access Token으로 업데이트
            }else {
                // Refresh Token도 유효하지 않은 경우
                System.out.println("Refresh_Token is Expired or Invalid");
                filterChain.doFilter(request, response);
                return;
            }
            // 6. 유효한 토큰일 경우 JwtTokenProvider의 getAuthentication() 호출하여 Authentication 생성
            Authentication authToken = jwtTokenProvider.getAuthentication(accessToken);
            // 7. 인증 정보를 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);
            // 8. 다음 필터로 요청 전달
            filterChain.doFilter(request, response);
        }
    }
}
