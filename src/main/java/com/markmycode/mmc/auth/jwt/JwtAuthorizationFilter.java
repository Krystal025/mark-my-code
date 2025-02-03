package com.markmycode.mmc.auth.jwt;

import com.markmycode.mmc.auth.dto.CustomUserDetails;
import com.markmycode.mmc.user.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 인증이 필요 없는 경로는 바로 필터 통과
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth/login") || requestURI.startsWith("/auth/refresh-token") || requestURI.startsWith("/user/signup")) {
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
        // 소셜 로그인 사용자는 이 필터가 적용되지 않도록 함
        String authType = jwtTokenProvider.getAuthType(accessToken);
        if("social".equals(authType)){
            filterChain.doFilter(request, response);
            return;
        }
        // Access 토큰 만료 여부 확인
        if(jwtTokenProvider.isExpired(accessToken)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access Token is Expired");
            System.out.println("Access Token is Expired");
            return;
        }
        // 유효한 토큰일 경우 해당 톰큰에서 사용자 정보 추출
        String userEmail = jwtTokenProvider.getUserEmail(accessToken);
        String userRole = jwtTokenProvider.getUserRole(accessToken);
        // 인증에 필요한 사용자 정보 객체 생성
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .userEmail(userEmail)
                .userPwd(null)
                .userRole(Role.valueOf(userRole))
                .build();
        // 인증된 사용자 정보를 나타내는 토큰 객체 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
        // 인증 정보를 SecurityContext라는 메모리에 저장하여 인증상태를 유지함
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request,response);
    }
}
