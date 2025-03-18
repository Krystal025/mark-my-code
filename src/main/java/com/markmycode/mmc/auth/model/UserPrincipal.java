package com.markmycode.mmc.auth.model;

import com.markmycode.mmc.user.dto.UserResponseDto;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserPrincipal {
    Long getUserId();
    Collection<? extends GrantedAuthority> getAuthorities();

    // 각 구현체에서 자신에 맞는 UserResponseDto를 생성하도록 구현
    UserResponseDto toResponseDto();
}
