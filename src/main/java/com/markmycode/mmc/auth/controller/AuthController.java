package com.markmycode.mmc.auth.controller;

import com.markmycode.mmc.auth.jwt.dto.LoginRequestDto;
import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.service.AuthService;
import com.markmycode.mmc.auth.oauth.service.TokenService;
import com.markmycode.mmc.config.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto,
                                                  HttpServletResponse response){ // JSON 형식으로 전달받은 정보를 dto로 변환
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto, response);
        // 쿠키 유효성 확인 후 유지 여부 결정 로직 필요
        CookieUtils.addCookie(response, "Refresh_Token", tokenResponseDto.getRefreshToken());
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + tokenResponseDto.getAccessToken())
                .body(tokenResponseDto);    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "Refresh_Token", required = false) String refreshToken){
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            // 서비스에서 새로운 Access Token 발급
            TokenResponseDto tokenResponseDto = tokenService.refreshAccessToken(refreshToken);
            // 컨트롤러에서 헤더 추가 후 응답
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + tokenResponseDto.getAccessToken()) // 컨트롤러에서 헤더 추가
                    .body(tokenResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
