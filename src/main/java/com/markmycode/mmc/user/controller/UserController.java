package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public String getUserDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("userId") Long userId,
                                Model model){
        // boolean isSocialLogin = userPrincipal instanceof CustomOAuth2User; // 겍체가 특정 클래스의 인스턴스인지 확인
        UserResponseDto user = userService.getUserById(userPrincipal.getUserId());
        boolean isMyPage = userId.equals(userPrincipal.getUserId());
        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("user", user);
        model.addAttribute("isMyPage", isMyPage);
        model.addAttribute("isAdmin", isAdmin);
        return "user_detail";
    }
}
