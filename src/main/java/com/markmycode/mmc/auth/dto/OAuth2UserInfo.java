package com.markmycode.mmc.auth.dto;

import com.markmycode.mmc.user.enums.Provider;
import com.markmycode.mmc.user.enums.Role;
import lombok.Builder;
import lombok.Getter;

// 소셜 로그인 사용자 정보를 애플리케이션에서 사용할 수 있도록 저장하는 DTO (데이터 전송 객체)
@Builder
@Getter
public class OAuth2UserInfo {

    private Long userId;
    private String userName; // 사용자 이름
    private String userEmail; // 사용자 이메일
    private Role userRole; // 사용자 권한
    private String socialId; // 소셜 제공자와 ID를 조합한 고유 식별자
    private Provider socialProvider; // 소셜 제공자

}
