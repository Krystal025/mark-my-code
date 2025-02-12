package com.markmycode.mmc.auth.jwt.service;

import com.markmycode.mmc.auth.jwt.dto.CustomUserDetails;
import com.markmycode.mmc.auth.jwt.dto.LoginRequestDto;
import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        try{
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

            // 사용자 정보 추출
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            // 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessJwt(
                    customUserDetails.getUserId(),
                    customUserDetails.getUsername(),
                    customUserDetails.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElse("ROLE_USER")
                    , null );
            String refreshToken = jwtTokenProvider.generateRefreshJwt(
                    customUserDetails.getUserId(),
                    null);
            System.out.println("AccessToken: Bearer " + accessToken);

            // 토큰 반환
            return new TokenResponseDto(accessToken, refreshToken);

        }catch (AuthenticationException e){
            throw new BadCredentialsException("Invalid email or password");
        }
    }

}
