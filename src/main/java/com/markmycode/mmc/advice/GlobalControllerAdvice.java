package com.markmycode.mmc.advice;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.user.dto.UserResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loginUser")
    public UserResponseDto addUserToModel(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        return null;    }

}
