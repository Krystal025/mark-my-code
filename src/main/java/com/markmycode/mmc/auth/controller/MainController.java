package com.markmycode.mmc.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/login_success")
    public ResponseEntity<String> home(HttpServletRequest request) {
        // 인증 여부 확인
        if (request.getAttribute("authenticated") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Unauthorized Access");
        }
        String message = "로그인 성공!";
        System.out.println(message); // 콘솔에 메시지 출력
        return ResponseEntity.ok(message); // REST API 응답으로 메시지 전송
    }

    @PostMapping("/login/oauth2/code/google")
    public ResponseEntity<?> loginSuccess(@RequestParam String token, HttpServletResponse response) {
        response.setHeader("Authorization", "Bearer " + token); // 응답 헤더에 저장
        response.addCookie(new Cookie("Access_Token", token));  // 쿠키에 저장
        return ResponseEntity.ok("로그인 성공");
    }


//    @GetMapping("/login_success")
//    public ResponseEntity<String> home() {
//        String message = "로그인 성공!";
//        System.out.println(message); // 콘솔에 메시지 출력
//        return ResponseEntity.ok(message); // REST API 응답으로 메시지 전송
//    }
}
