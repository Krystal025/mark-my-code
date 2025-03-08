package com.markmycode.mmc.auth.oauth.service;

import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.exception.custom.UnauthorizedException;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponseDto refreshAccessToken(String refreshToken) {

        Claims claims = null;
        try {
            claims = jwtTokenProvider.parseToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
        }
        // Claims가 null이 아니면 정상 처리
        if (claims != null) {
            Long userId = claims.get("userId", Long.class);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
            String userEmail = user.getUserEmail();
            String userRole = user.getUserRole().name();
            String socialId = user.getSocialId();
            // 새로 발급된 Access Token 생성
            String newAccessToken = jwtTokenProvider.generateAccessJwt(userId, userEmail, userRole, socialId);
            // 기존의 Refresh Token과 함께 새로 발급된 Access Token 반환
            return new TokenResponseDto(newAccessToken, refreshToken);
        }
        // Claims가 null 또는 잘못된 형식일 경우
        throw new BadRequestException(ErrorCode.INVALID_TOKEN);
    }
}
