package com.markmycode.mmc.auth.jwt.filter;

import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 일반 로그인 사용자 JWT 인가
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 인증이 필요 없는 경로는 바로 필터 통과
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/home")
                || requestURI.startsWith("/login")
                || requestURI.startsWith("/auth/**")
                || requestURI.startsWith("/oauth2/authorization")
                || requestURI.startsWith("/user/signup")
                || requestURI.startsWith("/post/**")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 헤더에서 Authorization에 있는 토큰 추출
        String accessToken = request.getHeader("Authorization");
        // 토큰이 없거나 "Bearer "로 시작하지 않으면 필터 체인 진행
        if(accessToken == null || !accessToken.startsWith("Bearer ")){
            System.out.println("Access Token is Null");
            filterChain.doFilter(request, response);
            return;
        }
        // "Bearer " 부분을 잘라내고 실제 토큰 부분만 추출
        accessToken = accessToken.substring(7);
        System.out.println("Extracted Access Token: " + accessToken);
        // 소셜 로그인 사용자는 이 필터가 적용되지 않도록 함
        String authType = jwtTokenProvider.getAuthType(accessToken);
        System.out.println("Auth Type: " + authType);
        if("social".equals(authType)){
            System.out.println("Social login detected. Skipping JWT filter.");
            filterChain.doFilter(request, response);
            return;
        }
        // Access 토큰 만료 여부 확인
        if(jwtTokenProvider.isExpired(accessToken)){
            System.out.println("Access Token is Expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access Token is Expired");
            return;
        }
        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            System.out.println("JWT Authentication success: " + authentication);
            // 인증 정보를 SecurityContext라는 메모리에 저장하여 인증상태를 유지함
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e){
            System.out.println("JWT Authentication failed: " + e.getMessage());
        }
        filterChain.doFilter(request,response);
    }
}
