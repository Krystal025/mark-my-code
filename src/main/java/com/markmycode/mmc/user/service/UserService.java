package com.markmycode.mmc.user.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.enums.Status;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 사용자 회원가입
    public void createUser(UserRequestDto userRequestDto){
        if(userRepository.existsByUserEmail(userRequestDto.getUserEmail())){
            throw new DuplicateException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if(userRepository.existsByUserNickname(userRequestDto.getUserNickname())){
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
        User user = User.builder()
                    .userName(userRequestDto.getUserName())
                    .userEmail(userRequestDto.getUserEmail())
                    .userPwd(passwordEncoder.encode(userRequestDto.getUserPwd()))
                    .userNickname(userRequestDto.getUserNickname())
                    .build();
        userRepository.save(user);

    }

    // 사용자 정보 수정
    @Transactional // 트랜잭션이 성공적으로 완료되면 변경사항이 자동으로 커밋되어 DB에 반영됨
    public void updateUser(Long userId, UserRequestDto userRequestDto){
        // 현재 인증된 사용자 이메일
        String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // 영속성 컨텍스트에 사용자가 존재하는지 확인
        User user = getUser(userId);
        // 요청한 사용자 이메일
        String userEmail = user.getUserEmail().replaceAll("\\s+", "").trim();
        // JWT에서 추출한 이메일과 요청 이메일 비교
        validateEmail(loggedInEmail, userEmail);
        // 닉네임 중복 체크
        if(userRequestDto.getUserNickname() != null &&
            userRepository.existsByUserNickname(userRequestDto.getUserNickname()) &&
            !userRequestDto.getUserNickname().equals(user.getUserNickname())){
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
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
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        // 현재 인증된 사용자 이메일
        String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // 영속성 컨텍스트에 사용자가 존재하는지 확인
        User user = getUser(userId);
        // 요청한 사용자 이메일
        String userEmail = user.getUserEmail().replaceAll("\\s+", "").trim();
        validateEmail(loggedInEmail, userEmail);
        if(!isAdmin){
            throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
        }
        User deactivateUser = user.toBuilder()
                .userStatus(Status.INACTIVE)
                .build();
        userRepository.save(deactivateUser);
    }

    // 사용자 정보 조회
    public UserResponseDto getUserById(Long userId){
        User user = getUser(userId);
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserEmail())
                .userCreatedAt(user.getUserCreatedAt())
                .build();
    }

    // 이메일 비교 메소드 (추후 AOP로 분리)
    private void validateEmail(String loggedInEmail, String requestedEmail) {
        if (!loggedInEmail.equals(requestedEmail)) {
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
