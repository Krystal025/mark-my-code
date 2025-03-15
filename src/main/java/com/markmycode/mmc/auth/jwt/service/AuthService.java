package com.markmycode.mmc.auth.jwt.service;

import com.markmycode.mmc.auth.jwt.dto.CustomUserDetails;
import com.markmycode.mmc.auth.jwt.dto.LoginRequestDto;
import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 일반 로그인 성공시 AccessToken + RefreshToken 발급
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        try{
            System.out.println("로그인 요청: 이메일 = " + loginRequestDto.getEmail() + ", 비밀번호 = " + loginRequestDto.getPassword());
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            System.out.println("인증 성공: " + authentication.isAuthenticated());
            // 사용자 정보 추출
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            System.out.println("로그인한 사용자: " + customUserDetails.getUsername());
            // 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessJwt(
                    customUserDetails.getUserId(),
                    customUserDetails.getUsername(),
                    customUserDetails.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElse("ROLE_USER"), null );
            String refreshToken = jwtTokenProvider.generateRefreshJwt(
                    customUserDetails.getUserId(), null);
            // 토큰 반환
            return new TokenResponseDto(accessToken, refreshToken);
        }catch (AuthenticationException e){
            throw new BadRequestException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
