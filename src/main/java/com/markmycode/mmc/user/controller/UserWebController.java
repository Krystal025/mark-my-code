package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserWebController {

    private final UserService userService;

//    @GetMapping("/{userId}")
//    public String getUser(@PathVariable("userId") Long userId, Model model){
//        UserResponseDto userResponseDto = userService.getUser(userId);
//        model.addAttribute("userInfo", userResponseDto);
//        return "user_info";
//    }
}
