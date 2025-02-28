package com.markmycode.mmc.auth.jwt.provider;

import com.markmycode.mmc.auth.oauth.dto.CustomOAuth2User;
import com.markmycode.mmc.auth.jwt.dto.CustomUserDetails;
import com.markmycode.mmc.auth.oauth.dto.OAuth2UserInfo;
import com.markmycode.mmc.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// JWT 생성 및 검증
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}")String secret){
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT Access 토큰 생성
    public String generateAccessJwt(Long userId, String userEmail, String userRole, String socialId){
        return Jwts.builder()
                .claim("userId", userId)
                .claim("userEmail", userEmail)
                .claim("userRole", userRole)
                .claim("socialId", socialId)
                .claim("authType", (socialId == null ? "basic" : "social"))
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발행시점 설정
                .expiration(new Date(System.currentTimeMillis() + 100 * 60 * 1000L)) // 토큰 유효시간 : 100분
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

    /*
     * JWT 토큰에서 사용자 정보를 추출하여 Authentication 객체를 생성하는 메소드.
     * 이 메소드가 호출되면 JWT 토큰을 파싱해서 사용자 이메일, 역할, (소셜 로그인일 경우) 소셜 ID 등을 추출하고,
     * 이 정보를 기반으로 UsernamePasswordAuthenticationToken을 생성하여 반환합니다.
     */

    public Authentication getAuthentication(String token) {
        // JWT 토큰에서 사용자 정보 추출
        Claims claims = parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String userEmail = claims.get("userEmail", String.class);
        String userRole = claims.get("userRole", String.class);
        String authType = claims.get("authType", String.class);

        System.out.println("Extracted Role from Token: " + userRole);  // 디버깅 로그

        // 사용자 권한 설정
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userRole));

        if("social".equals(authType)){
            // 소셜 로그인 사용자 객체 생성
            String socialId = claims.get("socialId", String.class);
            OAuth2UserInfo oAuth2Info = OAuth2UserInfo.builder()
                    .userId(userId)
                    .userEmail(userEmail)
                    .userName(userEmail) // 필요에 따라 다르게 설정 가능
                    .userRole(Role.valueOf(userRole))
                    .socialId(socialId)
                    .build();
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2Info);
            // 인증 토큰 생성 및 반환 (소셜 로그인)
            return new UsernamePasswordAuthenticationToken(customOAuth2User, null, authorities);
        }else{
            // 일반 로그인 사용자 객체 생성
            CustomUserDetails customUserDetails = CustomUserDetails.builder()
                    .userId(userId)
                    .userEmail(userEmail)
                    .userPwd(null) // JWT 토큰이므로 비밀번호 없음
                    .userRole(Role.valueOf(userRole))
                    .build();
            // 인증 토큰 생성 및 반환 (일반 로그인)
            return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
        }
    }
}
