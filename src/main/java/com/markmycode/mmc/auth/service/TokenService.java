package com.markmycode.mmc.auth.service;

import com.markmycode.mmc.auth.dto.TokenResponseDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto refreshAccessToken(String refreshToken) {
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
        } catch (
                ExpiredJwtException e) {
            // 로그에 토큰 만료 오류 추가
            throw new RuntimeException("Refresh Token expired");
        } catch (
                MalformedJwtException e) {
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
            String newAccessToken = jwtTokenProvider.generateAccessJwt(userId, userEmail, userRole, socialId);

            // 기존의 Refresh Token과 함께 새로 발급된 Access Token 반환
            return new TokenResponseDto(newAccessToken, refreshToken);
        }
        // claims가 null이면 예외 처리
        throw new RuntimeException("Failed to refresh Access Token");
    }
}
