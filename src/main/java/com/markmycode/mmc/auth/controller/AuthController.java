package com.markmycode.mmc.auth.controller;

import com.markmycode.mmc.auth.jwt.dto.LoginRequestDto;
import com.markmycode.mmc.auth.jwt.dto.TokenResponseDto;
import com.markmycode.mmc.auth.jwt.service.AuthService;
import com.markmycode.mmc.auth.oauth.service.TokenService;
import com.markmycode.mmc.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final TokenService tokenService;

    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto requestDto,
                        @RequestParam(value = "redirect", required = false) String redirectUrl,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        try {
            TokenResponseDto tokenResponseDto = authService.login(requestDto, response);
            redirectAttributes.addFlashAttribute("successMessage", "로그인 되었습니다");
            redirectAttributes.addAttribute("success", "true");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "이메일 또는 비밀번호가 잘못되었습니다");
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/auth/login";
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // JWT 쿠키 삭제 (만료된 쿠키로 설정)
        CookieUtils.deleteCookie(response, "Access_Token");
        return "redirect:/";  // 로그인 페이지로 리다이렉트
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("requestDto", new LoginRequestDto());
        return "users/login"; // 로그인 폼 페이지로 이동
    }

    // 구글 로그인 리다이렉트 처리
    @GetMapping("/google-login")
    public String googleLogin() {
        return "redirect:/oauth2/authorization/google"; // OAuth2 로그인 페이지로 리다이렉트
    }

    // 리프레시 토큰을 이용한 Access Token 재발급
    @PostMapping("/refresh-token")
    public String refreshAccessToken(@CookieValue(value = "Refresh_Token", required = false) String refreshToken,
                                     HttpServletResponse response, Model model) {
        if (refreshToken == null) {
            model.addAttribute("error", "리프레시 토큰이 없습니다.");
            return "users/login"; // 토큰이 없으면 로그인 페이지로
        }
        try {
            // 서비스에서 새로운 Access Token 발급
            TokenResponseDto tokenResponseDto = tokenService.refreshAccessToken(refreshToken);
            CookieUtils.addCookie(response, "Access_Token", tokenResponseDto.getAccessToken(), 30 * 60); // 30분 유효
            // Refresh Token을 쿠키에 추가
            CookieUtils.addCookie(response, "Refresh_Token", tokenResponseDto.getRefreshToken(), 7 * 24 * 60 * 60); // 7일 유효
            // 재발급 후, 다른 페이지로 리다이렉션
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Invalid Refresh Token");
            return "users/login"; // 토큰이 유효하지 않으면 로그인 페이지로
        }
    }
}
