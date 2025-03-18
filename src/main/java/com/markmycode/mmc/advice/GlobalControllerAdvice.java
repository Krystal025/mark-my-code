package com.markmycode.mmc.advice;

import com.markmycode.mmc.auth.jwt.dto.CustomUserDetails;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserRepository userRepository;

    @ModelAttribute("loginUser")
    public UserResponseDto addUserToModel() {
        // 현재 사용자의 인증 정보를 Spring Security의 SecurityContext에서 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자가 있고, 익명 사용자가 아닌 경우
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            // 인증된 사용자의 세부 정보를 CustomUserDetails 타입으로 가져옴
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // DB에서 최신 사용자 정보를 조회 (세션 기반이 아닌 실시간 데이터 반영을 위해 DB 조회)
            User user = userRepository.findByUserEmail(userDetails.getUserEmail());
            // 사용자 존재 시 최신 데이터로 UserResponseDto 생성
            if (user != null) {
                System.out.println("loginUser: " + user.getUserNickname());
               return UserResponseDto.builder()
                       .userId(user.getUserId())
                       .userNickname(user.getUserNickname())
                       .build();
            }
        }
        return null;
    }
}
