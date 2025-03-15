package com.markmycode.mmc.auth.controller;

import com.markmycode.mmc.auth.jwt.dto.LoginRequestDto;
import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.service.AuthService;
import com.markmycode.mmc.auth.oauth.service.TokenService;
import com.markmycode.mmc.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto,
                                                  HttpServletResponse response){ // JSON 형식으로 전달받은 정보를 dto로 변환
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto, response);
        // Access Token을 쿠키에 추가
        CookieUtils.addCookie(response, "Access_Token", tokenResponseDto.getAccessToken(), 30 * 60); // 30분 유효
        // Refresh Token을 쿠키에 추가
        CookieUtils.addCookie(response, "Refresh_Token", tokenResponseDto.getRefreshToken(), 7 * 24 * 60 * 60); // 7일 유효
        return ResponseEntity.ok().body(tokenResponseDto); // 헤더에 담지 않고, 응답 본문에만 담음
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "Refresh_Token", required = false) String refreshToken,
                                                HttpServletResponse response){
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            // 서비스에서 새로운 Access Token 발급
            TokenResponseDto tokenResponseDto = tokenService.refreshAccessToken(refreshToken);
            CookieUtils.addCookie(response, "Access_Token", tokenResponseDto.getAccessToken(), 30 * 60); // 30분 유효
            // Refresh Token을 쿠키에 추가
            CookieUtils.addCookie(response, "Refresh_Token", tokenResponseDto.getRefreshToken(), 7 * 24 * 60 * 60); // 7일 유효
            // 컨트롤러에서 헤더 추가 후 응답
            return ResponseEntity.ok().body(tokenResponseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
