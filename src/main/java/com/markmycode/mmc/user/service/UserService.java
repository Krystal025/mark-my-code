package com.markmycode.mmc.user.service;

import com.markmycode.mmc.auth.service.AuthService;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.post.repository.PostRepository;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.dto.UserUpdateDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.enums.Status;
import com.markmycode.mmc.user.repository.UserRepository;
import com.markmycode.mmc.util.EmailUtils;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;

    // 사용자 회원가입
    @Transactional
    public void createUser(UserRequestDto requestDto){
        String userEmail = EmailUtils.normalizeEmail(requestDto.getUserEmail());
        if(userRepository.existsByUserEmailAndUserStatus(userEmail, Status.ACTIVE)){
            throw new DuplicateException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (!requestDto.getUserPwd().equals(requestDto.getConfirmPwd())) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
        if(userRepository.existsByUserNicknameAndUserStatus(requestDto.getUserNickname(), Status.ACTIVE)){
            throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
        }
        String encodedPassword = passwordEncoder.encode(requestDto.getUserPwd());
        User user = User.fromDto(requestDto, encodedPassword);
        userRepository.save(user);
    }

    // 사용자 정보 수정
    @Transactional // 트랜잭션이 성공적으로 완료되면 변경사항이 자동으로 커밋되어 DB에 반영됨
    public void updateUser(Long loggedInUserId, Long userId, UserUpdateDto requestDto){
        // 영속성 컨텍스트에 사용자가 존재하는지 확인
        User user = getUser(userId);
        // JWT에서 추출한 이메일과 요청 이메일 비교
        validateUserOwnership(loggedInUserId, userId);
        boolean updated = false;
        // 닉네임 업데이트 (변경 시에만)
        if (requestDto.getUserNickname() != null &&
                !requestDto.getUserNickname().equals(user.getUserNickname())) {
            if (userRepository.existsByUserNicknameAndUserStatus(requestDto.getUserNickname(), Status.ACTIVE)) {
                throw new DuplicateException(ErrorCode.NICKNAME_ALREADY_EXIST);
            }
            user.updateNickname(requestDto.getUserNickname());
            updated = true;
        }
        // 비밀번호 업데이트
        boolean newPasswordProvided = requestDto.getUserPwd() != null && !requestDto.getUserPwd().isEmpty() &&
                requestDto.getConfirmPwd() != null && !requestDto.getConfirmPwd().isEmpty();
        boolean currentPasswordProvided = requestDto.getCurrentPwd() != null && !requestDto.getCurrentPwd().isEmpty();

        if (newPasswordProvided && !currentPasswordProvided) {
            throw new BadRequestException(ErrorCode.CURRENT_PASSWORD_REQUIRED);
        }
        if (currentPasswordProvided && newPasswordProvided) {
            if (!passwordEncoder.matches(requestDto.getCurrentPwd(), user.getUserPwd())) {
                throw new BadRequestException(ErrorCode.INVALID_CURRENT_PASSWORD);
            }
            if (!requestDto.getUserPwd().equals(requestDto.getConfirmPwd())) {
                throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
            }
            String encodedPassword = passwordEncoder.encode(requestDto.getUserPwd());
            user.updatePwd(encodedPassword);
            updated = true;
        }
        if (!updated) {
            throw new BadRequestException(ErrorCode.NO_CHANGES);
        }
    }

    // 사용자 비활성화(탈퇴)
    @Transactional
    public void deactivateUser(Long loggedInUserId, Long userId, HttpServletResponse response){
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
        // 게시글 삭제
        postRepository.deleteByUserUserId(userId);
        // 로그아웃 처리
        authService.logout(response);
    }

    // 사용자 정보 조회
    public UserResponseDto getUserById(Long userId){
        User user = getUser(userId);
        return user.toResponseDto();
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
