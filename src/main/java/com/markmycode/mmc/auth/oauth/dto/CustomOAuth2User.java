package com.markmycode.mmc.auth.oauth.dto;

import com.markmycode.mmc.config.model.UserPrincipal;
import com.markmycode.mmc.user.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Spring Security에서 인증을 처리하기 위해 OAuth2 사용자 정보를 변환하여 제공하는 인증 객체
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserPrincipal {
    private final OAuth2UserInfo oAuth2UserInfo;

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    // 사용자 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return oAuth2UserInfo.getUserRole().toString();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getUserName();
    }

    public Long getUserId(){ return oAuth2UserInfo.getUserId(); }

    public String getUserEmail(){ return oAuth2UserInfo.getUserEmail(); }

    public String getSocialId(){ return oAuth2UserInfo.getSocialId(); }

    public Provider getSocialProvider(){ return oAuth2UserInfo.getSocialProvider(); }

    public static CustomOAuth2User fromOAuth2UserInfo(OAuth2UserInfo oAuth2UserInfo) {
        return new CustomOAuth2User(oAuth2UserInfo);
    }

}
