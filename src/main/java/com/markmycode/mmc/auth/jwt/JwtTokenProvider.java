package com.markmycode.mmc.auth.jwt;

import com.markmycode.mmc.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository){
        this.secretKey = Jwts.SIG.HS256.key().build();
        this.userRepository = userRepository;
    }

    // JWT 토큰에서 사용자 이메일 추출
    public String getUserEmail(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userEmail", String.class);
    }

    // JWT 토큰에서 사용자 역할 추출
    public String getUserRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userRole", String.class);
    }

    // JWT 토큰에서 소셜 ID 추출
    public String getSocialId(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("socialId", String.class);
    }

    // JWT 토큰에서 로그인 방식 추출
    public String getAuthType(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("authType", String.class);
    }

    // JWT 토큰의 유효기간 추출 및 현재 시간과의 비교를 통한 만료 여부 확인
    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // JWT Access 토큰 생성
    public String generateAccessJwt(String userEmail, String userRole, String authType){
        return Jwts.builder()
                .claim("userEmail", userEmail)
                .claim("userRole", userRole)
                .claim("authType", (authType == null ? "basic" : "social"))
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발행시점 설정
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000L)) // 토큰 유효시간 : 10분
                .signWith(secretKey) // JWT 서명(signature)시 사용할 암호키 지정
                .compact(); // JWT의 Header, Payload, Signature를 결합하여 문자열로 반환
    }

    // JWT Refresh 토큰 생성
    public String generateRefreshJwt(Long userId, String authType){
        return Jwts.builder()
                .claim("userId", userId)
                .claim("authType", (authType == null ? "basic" : "social"))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L)) // 토큰 유효시간 : 30일
                .signWith(secretKey)
                .compact();
    }

}
