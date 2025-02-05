package com.markmycode.mmc.auth.controller;

import com.markmycode.mmc.auth.dto.LoginRequestDto;
import com.markmycode.mmc.auth.dto.TokenResponseDto;
import com.markmycode.mmc.auth.service.AuthService;
import com.markmycode.mmc.auth.service.TokenService;
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
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){ // JSON 형식으로 전달받은 정보를 dto로 변환
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(tokenResponseDto);
    }

    // Refresh Token을 받아서 새로운 Access Token 발급 (추후 @RequestHeader로 변경 고려)
//    @PostMapping("/refresh-token")
//    public ResponseEntity<TokenResponseDto> refreshAccessToken(@RequestParam("refreshToken") String refreshToken){
//        TokenResponseDto tokenResponseDto = authService.refreshAccessToken(refreshToken);
//        return ResponseEntity.ok(tokenResponseDto);
//    }

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
