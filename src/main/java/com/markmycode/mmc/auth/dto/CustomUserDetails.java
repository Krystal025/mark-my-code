package com.markmycode.mmc.auth.dto;

import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Spring Security에서 인증/인가 작업에 필요한 정보를 제공하기 위해 사용자 정보를 담는 DTO
@Getter
@Builder
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, UserPrincipal {

    private final Long userId;
    private final String userEmail;
    private final String userPwd;
    private final Role userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Enum의 name()을 사용하여 문자열로 상수값을 가져오고, 권한으로 변환함
        return List.of(userRole::name);
    }

    @Override
    public String getPassword() {
        return userPwd;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // User 엔티티로부터 CustomUserDetails 생성
    public static CustomUserDetails fromUser(User user) {
        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userPwd(user.getUserPwd())
                .userRole(user.getUserRole())
                .build();
    }

}
