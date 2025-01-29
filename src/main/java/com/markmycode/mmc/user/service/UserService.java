package com.markmycode.mmc.user.service;

import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.enums.Status;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 사용자 회원가입
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

    // 사용자 정보 조회
    public UserResponseDto getUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);
        return UserResponseDto.builder()
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserEmail())
                .userCreatedAt(user.getUserCreatedAt())
                .build();
    }

    // 사용자 정보 수정
    @Transactional // 트랜잭션이 성공적으로 완료되면 변경사항이 자동으로 커밋되어 DB에 반영됨
    public void updateUser(Long userId, UserRequestDto userRequestDto){
        // 영속성 컨텍스트에 사용자가 존재하는지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);
        // 닉네임 중복 체크
        if(userRequestDto.getUserNickname() != null &&
            userRepository.existsByUserNickname(userRequestDto.getUserNickname()) &&
            !userRequestDto.getUserNickname().equals(user.getUserNickname())){
            throw new RuntimeException();
        }
        // 기존 객체의 상태를 유지하면서 일부 필드만 수정
        User updatedUser = user.toBuilder() // 기존 객체를 기반으로 빌더 사용 (일부 필드 수정 가능)
                .userPwd(userRequestDto.getUserPwd() != null ? userRequestDto.getUserPwd() : user.getUserPwd())
                .userNickname(userRequestDto.getUserNickname() != null ? userRequestDto.getUserNickname() : user.getUserNickname())
                .build();
        userRepository.save(updatedUser);
    }

    // 사용자 비활성화(탈퇴)
    @Transactional
    public void deactivateUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);

        User deactivateUser = user.toBuilder()
                .userStatus(Status.INACTIVE)
                .build();
        userRepository.save(deactivateUser);
    }
}
