package com.markmycode.mmc.auth.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserPrincipal {
    Long getUserId();
    Collection<? extends GrantedAuthority> getAuthorities();
}
