package com.markmycode.mmc.auth.service;

import com.markmycode.mmc.auth.dto.CustomUserDetails;
import com.markmycode.mmc.auth.dto.LoginRequestDto;
import com.markmycode.mmc.auth.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.JwtTokenProvider;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public TokenResponseDto login(LoginRequestDto loginRequestDto){
        try{
            System.out.println("로그인 시도: " + loginRequestDto.getEmail());
            // 이메일과 비밀번호로 인증 요청
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            System.out.println("인증 완료: " + authentication);
            // 인증된 사용자 정보 가져오기
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = customUserDetails.getUsername();
            String userRole = customUserDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER"); // 기본 역할 설정
            String accessToken = jwtTokenProvider.generateAccessJwt(userEmail, userRole, null );
            String refreshToken = jwtTokenProvider.generateRefreshJwt(customUserDetails.getUserId(), null);
            System.out.println("JWT 발급 완료");
            // 토큰 반환
            return new TokenResponseDto(accessToken, refreshToken);
        }catch (AuthenticationException e){
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    // Access Token 재발급 요청은 서비스 로직에서 (JwtTokenProvider는 JWT 검증 및 생성만)
    public TokenResponseDto refreshAccessToken(String refreshToken){
//        try{
//            Claims claims = jwtTokenProvider.parseToken(refreshToken);
//
//            Long userId = claims.get("userId", Long.class);
//            // 사용자 정보 가져오기
//            User user = userRepository.findById(userId)
//                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
//            String userEmail = user.getUserEmail();
//            String userRole = user.getUserRole().name();
//            String socialId = user.getSocialId();
//            // 새로 발급된 Access Token 생성
//            String newAccessToken = jwtTokenProvider.generateAccessJwt(userEmail, userRole, socialId);
//            // 기존의 Refresh Token과 함께 새로 발급된 Access Token 반환
//            return new TokenResponseDto(newAccessToken, refreshToken);
//        }catch (Exception e){
//            throw new RuntimeException("Failed to refresh Access Token");
//        }
        Claims claims = null;
        try {
            claims = jwtTokenProvider.parseToken(refreshToken);
        } catch (ExpiredJwtException e) {
            // 로그에 토큰 만료 오류 추가
            throw new RuntimeException("Refresh Token expired");
        } catch (MalformedJwtException e) {
            // 로그에 토큰 형식 오류 추가
            throw new RuntimeException("Invalid Refresh Token");
        } catch (Exception e) {
            // 다른 오류 처리
            throw new RuntimeException("Failed to refresh Access Token");
        }

        // claims가 null이 아니면 정상 처리
        if (claims != null) {
            Long userId = claims.get("userId", Long.class);
            // 사용자 정보 가져오기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            String userEmail = user.getUserEmail();
            String userRole = user.getUserRole().name();
            String socialId = user.getSocialId();

            // 새로 발급된 Access Token 생성
            String newAccessToken = jwtTokenProvider.generateAccessJwt(userEmail, userRole, socialId);

            // 기존의 Refresh Token과 함께 새로 발급된 Access Token 반환
            return new TokenResponseDto(newAccessToken, refreshToken);
        }

        // claims가 null이면 예외 처리
        throw new RuntimeException("Failed to refresh Access Token");
    }
}
