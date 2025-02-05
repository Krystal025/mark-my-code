package com.markmycode.mmc.auth.service;

import com.markmycode.mmc.auth.dto.CustomUserDetails;
import com.markmycode.mmc.auth.dto.LoginRequestDto;
import com.markmycode.mmc.auth.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.JwtTokenProvider;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final UserRepository userRepository;

    // 일반 로그인 성공시 AccessToken + RefreshToken 발급
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

}
