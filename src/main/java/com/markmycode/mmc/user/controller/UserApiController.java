package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto userRequestDto){
        userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered");
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable("userId") Long userId,
                                             @RequestBody UserRequestDto userRequestDto){
        userService.updateUser(userId, userRequestDto);
        return ResponseEntity.ok("User Information Updated");
    }


    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable("userId") Long userId){
        userService.deactivateUser(userId);
        return ResponseEntity.ok("User Deactivated");
    }
}
