package com.markmycode.mmc.auth.jwt.filter;

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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

// 일반 로그인 사용자 JWT 인가
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    private static final Set<String> EXCLUDED_URLS = Set.of(
            "/home",
            "/login",
            "/auth",
            "/oauth2/authorization",
            "/users/signup",
            "/posts",
            "/comments"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 인증이 필요 없는 경로는 바로 필터 통과
        String requestURI = request.getRequestURI();
        // 🔥 로그인 요청은 필터링 제외
        if (requestURI.startsWith("/home")
                || requestURI.startsWith("/login")
                || requestURI.startsWith("/auth/")
                || requestURI.startsWith("/oauth2/authorization")
                || requestURI.startsWith("/users/signup")
                || (requestURI.startsWith("/posts/") || requestURI.startsWith("/comments/")) && request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 여기까지 왔다면, JWT 검증 진행
        System.out.println("🔍 JWT 검사 진행 중: " + requestURI);

        // 쿠키에서 Access Token 추출
        String accessToken = CookieUtils.getCookie(request, "Access_Token");

        // 토큰 존재 여부 확인
        if (accessToken == null) {
            System.out.println("Access Token is Null");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token is missing");
            return;
        }

        // 토큰 만료 여부 확인 및 리프레시
        if (jwtTokenProvider.isExpired(accessToken)) {
            String refreshToken = CookieUtils.getCookie(request, "Refresh_Token");

            if (refreshToken != null && !jwtTokenProvider.isExpired(refreshToken)) {
                TokenResponseDto newToken = tokenService.refreshAccessToken(refreshToken);
                // 새 토큰을 쿠키에 설정
                CookieUtils.addCookie(response, "Access_Token", newToken.getAccessToken());
                CookieUtils.addCookie(response, "Refresh_Token", newToken.getRefreshToken());
                accessToken = newToken.getAccessToken();
            } else {
                // 리프레시 토큰도 만료된 경우
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }
        }

        // 소셜 로그인 사용자 필터링
        if("social".equals(jwtTokenProvider.getAuthType(accessToken))){
            System.out.println("Social login detected. Skipping JWT filter.");
            filterChain.doFilter(request, response);
            return;
        }

        // 인증 정보 설정
        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        // 필터 체인 진행
        filterChain.doFilter(request,response);
    }

}
