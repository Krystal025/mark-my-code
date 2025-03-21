package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 등록
    @PostMapping
    public String createUser(@Valid @ModelAttribute("requestDto") UserRequestDto requestDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            return "users/signup";
        }
        if (!requestDto.getUserPwd().equals(requestDto.getConfirmPwd())){
            bindingResult.reject("password.mismatch", "비밀번호가 일치하지 않습니다.");
            return "users/signup";
        }
        try {
            userService.createUser(requestDto);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 성공적으로 완료되었습니다!");
            return "redirect:/auth/login";
        } catch (DuplicateException e) {
            bindingResult.reject(e.getErrorCode().name(), e.getMessage());
            return "users/signup";
        }
    }

    // 사용자 정보 수정
    @PostMapping("/{userId}/update")
    public String updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable("userId") Long userId,
                             @ModelAttribute UserRequestDto requestDto){
        userService.updateUser(userPrincipal.getUserId(),userId,requestDto);
        return "redirect:/users/" + userId;
    }

    // 사용자 프로필 조회
    @GetMapping("/{userId}")
    public String getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
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

    // 사용자 등록 폼 조회
    @GetMapping("/signup")
    public String getSignupForm(Model model){
        model.addAttribute("requestDto", new UserRequestDto());
        return "users/signup";
    }

    // 사용자 정보 수정 폼 조회
    @GetMapping("/{userId}/edit")
    public String getUserUpdateForm(@PathVariable("userId") Long userId, Model model){
        UserResponseDto user = userService.getUserById(userId);
        UserRequestDto requestDto = UserRequestDto.builder()
                .userNickname(user.getUserNickname())
                .build();
        model.addAttribute("user", user);
        model.addAttribute("requestDto", requestDto);

        return "users/edit";
    }

}
