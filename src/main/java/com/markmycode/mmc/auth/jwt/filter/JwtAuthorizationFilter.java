package com.markmycode.mmc.auth.jwt.filter;

import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.auth.oauth.service.TokenService;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.UnauthorizedException;
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
            "/auth/**",
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
        if (requestURI.startsWith("/")
                || requestURI.startsWith("/home")
                || requestURI.startsWith("/login")
                || requestURI.startsWith("/auth/") && request.getMethod().equals("GET")
                || requestURI.startsWith("/oauth2/authorization")
                || requestURI.startsWith("/users/signup")
                || (requestURI.startsWith("/posts/")
                || requestURI.startsWith("/comments/")) && request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 Access Token 추출
        String accessToken = CookieUtils.getCookie(request, "Access_Token");
        // 토큰 존재 여부 확인
        if (accessToken == null || accessToken.isEmpty()) {
            throw new UnauthorizedException(ErrorCode.ACCESS_TOKEN_MISSING);
        }
        // 토큰 만료 여부 확인 및 리프레시
        try {
            jwtTokenProvider.isExpired(accessToken);
            System.out.println("Access_Token is still valid: " + accessToken);
        } catch (UnauthorizedException ex) {
            System.out.println("Access_Token is Expired: " + accessToken);
            // Refresh Token 가져오기
            String refreshToken = CookieUtils.getCookie(request, "Refresh_Token");
            try {
                // 리프레시 토큰 검증
                jwtTokenProvider.isExpired(refreshToken);
                // 새 액세스 토큰 발급
                TokenResponseDto newToken = tokenService.refreshAccessToken(refreshToken);
                // 쿠키에 새 토큰 저장
                CookieUtils.addCookie(response, "Access_Token", newToken.getAccessToken(), 30 * 60); // 30분 유효
                CookieUtils.addCookie(response, "Refresh_Token", newToken.getRefreshToken(), 7 * 24 * 60 * 60); // 7일 유효
                accessToken = newToken.getAccessToken();
                System.out.println("New Access_Token is Generated");
            } catch (UnauthorizedException e) {
                // 리프레시 토큰도 만료됨
                throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
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
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        }
        // 필터 체인 진행
        filterChain.doFilter(request,response);
    }

}
