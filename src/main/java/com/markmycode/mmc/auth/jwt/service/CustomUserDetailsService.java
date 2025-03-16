package com.markmycode.mmc.auth.jwt.service;

import com.markmycode.mmc.auth.jwt.dto.CustomUserDetails;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(userEmail);
        if(user == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        System.out.println("🔍 Stored Password (DB에서 가져온 값): " + user.getUserPwd());

        return CustomUserDetails.fromUser(user);
    }
}
