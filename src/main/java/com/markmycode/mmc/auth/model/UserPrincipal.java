package com.markmycode.mmc.auth.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserPrincipal {
    Long getUserId();
    String getUserEmail();  // 추가된 메서드
    // String getUserName();   // 추가된 메서드
    Collection<? extends GrantedAuthority> getAuthorities();
}
