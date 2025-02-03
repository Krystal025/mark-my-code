package com.markmycode.mmc.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}")String secret){
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        //this.secretKey = Jwts.SIG.HS256.key().build();
    }

    // JWT Access 토큰 생성
    public String generateAccessJwt(String userEmail, String userRole, String authType){
        return Jwts.builder()
                .claim("userEmail", userEmail)
                .claim("userRole", userRole)
                .claim("authType", (authType == null ? "basic" : "social"))
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발행시점 설정
                .expiration(new Date(System.currentTimeMillis() + 1 * 60 * 1000L)) // 토큰 유효시간 : 10분
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

    // JWT 토큰 검증 메소드
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)  // Deprecated된 방식
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // JWT 토큰에서 사용자 이메일 추출
    public String getUserEmail(String token){
        return parseToken(token).get("userEmail", String.class);
    }

    // JWT 토큰에서 사용자 역할 추출
    public String getUserRole(String token){
        return parseToken(token).get("userRole", String.class);
    }

    // JWT 토큰에서 소셜 ID 추출
    public String getSocialId(String token){
        return parseToken(token).get("socialId", String.class);
    }

    // JWT 토큰에서 로그인 방식 추출
    public String getAuthType(String token){
        return parseToken(token).get("authType", String.class);
    }

    // JWT 토큰의 유효기간 추출 및 현재 시간과의 비교를 통한 만료 여부 확인
    public Boolean isExpired(String token){
        return parseToken(token).getExpiration().before(new Date());
    }

}
