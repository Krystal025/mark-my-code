package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.dto.UserUpdateDto;
import com.markmycode.mmc.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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
        // 유효성 검사 (실패 시 기존 입력값 유지)
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }
        try {
            userService.createUser(requestDto);
            redirectAttributes.addFlashAttribute("signupSuccess", "회원가입이 성공적으로 완료되었습니다");
            return "redirect:/auth/login";
        } catch (ForbiddenException e) {
            bindingResult.reject(e.getErrorCode().name(), e.getMessage());
            return "users/signup";
        } catch (DuplicateException e) {
            bindingResult.reject(e.getErrorCode().name(), e.getMessage());
            return "users/signup";
        }
    }

    // 사용자 정보 수정
    @PostMapping("/{userId}/update")
    public String updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable("userId") Long userId,
                             @Valid @ModelAttribute("requestDto") UserUpdateDto requestDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes){
        // 유효성 검사 (실패 시 기존 입력값 유지)
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        try {
            userService.updateUser(userPrincipal.getUserId(),userId,requestDto);
            redirectAttributes.addFlashAttribute("updateSuccess", "회원 정보가 성공적으로 수정되었습니다");
            return "redirect:/users/" + userId;
        } catch (ForbiddenException e) {
            bindingResult.reject(e.getErrorCode().name(), e.getMessage());
            return "users/edit";
        } catch (DuplicateException e) {
            bindingResult.reject(e.getErrorCode().name(), e.getMessage());
            return "users/edit";
        }catch (BadRequestException e) {
//            if (e.getErrorCode() == ErrorCode.NO_CHANGES) {
//                redirectAttributes.addFlashAttribute("noUpdate", "변경사항이 없습니다.");
//                return "users/edit";
//            }
            bindingResult.reject(e.getErrorCode().name(), e.getMessage()); // 글로벌 처리
            return "users/edit";
        }
    }

    @PostMapping("/{userId}/deactivate")
    public String deactivateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                 @PathVariable("userId") Long userId,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes){
        userService.deactivateUser(userPrincipal.getUserId(), userId, response);
        redirectAttributes.addFlashAttribute("deactivateSuccess", "회원 탈퇴가 완료되었습니다.");
        return "redirect:/";
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
        UserUpdateDto requestDto = UserUpdateDto.builder()
                .userNickname(user.getUserNickname())
                .build();
        model.addAttribute("user", user);
        model.addAttribute("requestDto", requestDto);

        return "users/edit";
    }

}
