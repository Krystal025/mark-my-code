package com.markmycode.mmc.admin.controller;

import com.markmycode.mmc.admin.service.AdminService;
import com.markmycode.mmc.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    private String getUserList(Model model){
        List<UserResponseDto> userList = adminService.getUserList();
        model.addAttribute("userList", userList);
        return "admin";
    }
}
