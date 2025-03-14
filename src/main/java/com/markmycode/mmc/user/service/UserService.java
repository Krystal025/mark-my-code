package com.markmycode.mmc.user.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import com.markmycode.mmc.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 사용자 회원가입
    public void createUser(UserRequestDto requestDto){
        String userEmail = EmailUtils.normalizeEmail(requestDto.getUserEmail());
        if(userRepository.existsByUserEmail(userEmail)){
            throw new DuplicateException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if(userRepository.existsByUserNickname(requestDto.getUserNickname())){
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
        User user = User.builder()
                    .userName(requestDto.getUserName())
                    .userEmail(userEmail)
                    .userPwd(passwordEncoder.encode(requestDto.getUserPwd()))
                    .userNickname(requestDto.getUserNickname())
                    .build();
        userRepository.save(user);

    }

    // 사용자 정보 수정
    @Transactional // 트랜잭션이 성공적으로 완료되면 변경사항이 자동으로 커밋되어 DB에 반영됨
    public void updateUser(Long loggedInUserId, Long userId, UserRequestDto requestDto){
        // 영속성 컨텍스트에 사용자가 존재하는지 확인
        User user = getUser(userId);
        // JWT에서 추출한 이메일과 요청 이메일 비교
        validateUserOwnership(loggedInUserId, userId);
        // 닉네임 중복 체크
        if(requestDto.getUserNickname() != null  // 새로운 닉네임 요청이 들어왔는지
                && !requestDto.getUserNickname().equals(user.getUserNickname()) // 사용자의 기존 닉네임과 다른지
                && userRepository.existsByUserNickname(requestDto.getUserNickname())){ // 다른 사용자가 사용 중인 닉네임이 아닌지
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
//        if (requestDto.getUserPwd() != null){
//            user.updatePwd(passwordEncoder.encode(requestDto.getUserPwd()));
//        }
        if (requestDto.getUserNickname() != null){
            user.updateNickname(requestDto.getUserNickname());
        }
    }

    // 사용자 비활성화(탈퇴)
    @Transactional
    public void deactivateUser(Long loggedInUserId, Long userId){
        // 사용자 조회
        User user = getUser(userId);
        // 관리자 여부 확인
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        // 관리자일 경우 비활성화 처리 가능
        if (isAdmin){
            user.deactivate();
            return;
        }
        validateUserOwnership(loggedInUserId, userId);
        // 본인 계정 비활성화(탈퇴)
        user.deactivate();
    }

    // 사용자 정보 조회
    public UserResponseDto getUserById(Long userId){
        User user = getUser(userId);
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userStatus(user.getUserStatus())
                .userCreatedAt(user.getUserCreatedAt())
                .build();
    }

    // 해당 ID에 대한 엔티티 객체 반환
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    // 사용자 소유권 검증
    private void validateUserOwnership(Long loggedInUserId, Long userId) {
        if (!Objects.equals(loggedInUserId, userId)) {
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }

}
