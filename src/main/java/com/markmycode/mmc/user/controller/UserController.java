package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public String getUserDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("userId") Long userId,
                                Model model){
        UserResponseDto user = userService.getUserById(userPrincipal.getUserId());
        boolean isProfileOwner = userId.equals(userPrincipal.getUserId());
        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("user", user);
        model.addAttribute("isProfileOwner", isProfileOwner);
        model.addAttribute("isAdmin", isAdmin);

        return "users/profile";
    }


    @GetMapping("/update-form/{userId}")
    public String getUserUpdateForm(@PathVariable("userId") Long userId, Model model){
        UserResponseDto user = userService.getUserById(userId);
        UserRequestDto requestDto = UserRequestDto.builder()
                .userNickname(user.getUserNickname())
                .build();
        model.addAttribute("user", user);
        model.addAttribute("requestDto", requestDto);

        return "users/update-form";
    }

    @PostMapping("/update/{userId}")
    public String updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable("userId") Long userId,
                             @ModelAttribute UserRequestDto requestDto){
        userService.updateUser(userPrincipal.getUserId(),userId,requestDto);
        return "redirect:/users/" + userId;
    }
}
