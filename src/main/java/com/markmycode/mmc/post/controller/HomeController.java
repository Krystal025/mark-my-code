package com.markmycode.mmc.post.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login_success")
    public String homePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            String userEmail = oAuth2User.getName();
            model.addAttribute("userEmail", userEmail);
        }
        return "login_success";
    }
}
