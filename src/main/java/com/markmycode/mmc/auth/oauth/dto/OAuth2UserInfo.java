package com.markmycode.mmc.auth.oauth.dto;

import com.markmycode.mmc.enums.Provider;
import com.markmycode.mmc.enums.Role;
import com.markmycode.mmc.enums.Status;
import lombok.Builder;
import lombok.Getter;

// 소셜 로그인 후, 소셜 제공자가 인증을 처리하고 반환한 사용자 정보를 애플리케이션 내에서 사용할 수 있도록 변환한 DTO
@Getter
@Builder
public class OAuth2UserInfo {

    private Long userId;
    private String userName;
    private String userEmail;
    private String userNickname;
    private Status userStatus;
    private Role userRole;
    private String socialId; // 소셜 제공자와 ID를 조합한 고유 식별자
    private Provider socialProvider; // 소셜 제공자

}
