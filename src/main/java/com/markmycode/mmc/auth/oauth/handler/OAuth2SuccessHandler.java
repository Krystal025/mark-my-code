package com.markmycode.mmc.auth.oauth.handler;

import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.auth.oauth.dto.CustomOAuth2User;
import com.markmycode.mmc.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// OAuth2 로그인 성공 시 JWT 토큰 생성 및 클라이언트 리다이렉트 처리 클래스
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    // 소셜 로그인 성공시 AccessToken + RefreshToken 토큰 발급
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 인증을 통해 얻은 사용자 정보를 CustomOAuth2User 객체로 반환
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        // SecurityContextHolder에 Authentication 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 소셜 로그인 사용자 정보 추출
        Long userId = customOAuth2User.getUserId();
        String userEmail = customOAuth2User.getUserEmail();
        String socialId = customOAuth2User.getSocialId();
        String userRole = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("No authority found"));

        // 사용자 정보를 바탕으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessJwt(userId, userEmail, userRole, socialId);
        String refreshToken = jwtTokenProvider.generateRefreshJwt(customOAuth2User.getUserId(), "social");

        // 액세스 토큰과 리프레시 토큰을 httpOnly 쿠키에 저장
        CookieUtils.addCookie(response, "Access_Token", accessToken, 30 * 60); // 30분 유효
        CookieUtils.addCookie(response, "Refresh_Token", refreshToken, 7 * 24 * 60 * 60); // 7일 유효

        // 사용자가 로그인 후 이동할 페이지로 리다이렉트
        response.sendRedirect("http://localhost:8080/login_success");
    }

}
