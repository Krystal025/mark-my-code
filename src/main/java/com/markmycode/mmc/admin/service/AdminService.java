package com.markmycode.mmc.admin.service;

import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.enums.Status;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserResponseDto> getUserList(){
        return userRepository.findAll() // DB에서 모든 엔티티(User)를 가져와 List<User>로 반환
                .stream() // List<User>를 스트림으로 변환
                .filter(user -> user.getUserStatus() == Status.ACTIVE) // 상태가 ACTIVE인 사용자만 필터링
                .map(user -> UserResponseDto.builder() // 스트림의 각 요소를 변환 (User 객체를 매개변수로 받아 UserDto로 변환)
                        .userId(user.getUserId())
                        .userName(user.getUserName())
                        .userEmail(user.getUserEmail())
                        .userNickname(user.getUserNickname())
                        .userStatus(user.getUserStatus())
                        .userCreatedAt(user.getUserCreatedAt())
                        .build())
                .toList();// 스트림의 요소를 수집하여 List 컬렉션 타입으로 변환
    }
}
