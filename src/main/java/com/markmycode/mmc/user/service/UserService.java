package com.markmycode.mmc.user.service;

import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(UserRequestDto userRequestDto){
        if(userRepository.existsByUserEmail(userRequestDto.getUserEmail())){
            System.out.println("Existed Email");
        }
        if(userRepository.existsByUserNickname(userRequestDto.getUserNickname())){
            System.out.println("Existed Nickname");
        }
        try{
            User user = User.builder()
                    .userName(userRequestDto.getUserName())
                    .userEmail(userRequestDto.getUserEmail())
                    .userPwd(userRequestDto.getUserPwd())
                    .userNickname(userRequestDto.getUserNickname())
                    .build();
            userRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("User not created");
        }
    }

}
