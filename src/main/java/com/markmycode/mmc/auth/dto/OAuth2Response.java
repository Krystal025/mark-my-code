package com.markmycode.mmc.auth.dto;

public interface OAuth2Response {
    // 제공자
    String getSocialProvider();
    // 제공자에서 발급해주는 아이디
    String getSocialProviderId();
    // 이메일
    String getUserEmail();
    // 사용자 이름
    String getUserName();
}
