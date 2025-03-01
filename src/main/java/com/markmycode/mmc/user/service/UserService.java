package com.markmycode.mmc.user.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.entity.User;
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
    public void updateUser(Long userId, UserRequestDto requestDto){
        // 현재 인증된 사용자 이메일
        String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // 영속성 컨텍스트에 사용자가 존재하는지 확인
        User user = getUser(userId);
        // 요청한 사용자 이메일
        String userEmail = user.getUserEmail().replaceAll("\\s+", "").trim();
        // JWT에서 추출한 이메일과 요청 이메일 비교
        validateEmail(loggedInEmail, userEmail);
        // 닉네임 중복 체크
        if(requestDto.getUserNickname() != null && // 새로운 닉네임 요청이 들어왔는지
                !requestDto.getUserNickname().equals(user.getUserNickname()) && // 사용자의 기존 닉네임과 다른지
                userRepository.existsByUserNickname(requestDto.getUserNickname())){ // 다른 사용자가 사용 중인 닉네임이 아닌지
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
        // 변경된 필드만 반영
        if (requestDto.getUserNickname() != null){
            user.updateNickname(requestDto.getUserNickname());
        }
        if (requestDto.getUserPwd() != null){
            user.updatePwd(passwordEncoder.encode(requestDto.getUserPwd()));
        }
        // JPA dirty checking으로 DB에 자동 반영
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
        // JPA dirty checking으로 DB에 자동 반영
        user.deactivate();
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

    // 해당 ID에 대한 엔티티 객체 반환
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
