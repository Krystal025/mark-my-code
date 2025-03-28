package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.dto.UserUpdateDto;
import com.markmycode.mmc.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto userRequestDto){
        userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered");
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable("userId") Long userId,
                                             @RequestBody UserUpdateDto requestDto){
        userService.updateUser(userPrincipal.getUserId(), userId, requestDto);
        return ResponseEntity.ok("User Information Updated");
    }


    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable("userId") Long userId,
                                                 HttpServletResponse response){
        userService.deactivateUser(userPrincipal.getUserId(), userId, response);
        return ResponseEntity.ok("User Deactivated");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId){
        UserResponseDto userResponseDto = userService.getUserById(userId);
        return ResponseEntity.ok(userResponseDto);
    }
}
