package com.markmycode.mmc.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// OAuth2(소셜 제공자)에게 사용자의 인증 요청을 할 때 필요한 사용자 정보를 담는 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2RequestDto {
    private String userName;
    private String userEmail;
}
