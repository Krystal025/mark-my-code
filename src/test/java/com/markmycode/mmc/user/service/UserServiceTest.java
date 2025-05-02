package com.markmycode.mmc.user.service;

import com.markmycode.mmc.auth.service.AuthService;
import com.markmycode.mmc.enums.Status;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.DuplicateException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.post.repository.PostRepository;
import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.dto.UserUpdateDto;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AuthService authService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDto requestDto;
    private UserUpdateDto updateDto;
    private User user;

    @BeforeEach
    void setUp() {
        requestDto = UserRequestDto.builder()
                .userName("테스트 유저")
                .userEmail("test@example.com")
                .userPwd("password123")
                .confirmPwd("password123")
                .userNickname("tester")
                .build();

        updateDto = UserUpdateDto.builder()
                .currentPwd("oldPassword")
                .userPwd("newPassword123")
                .confirmPwd("newPassword123")
                .userNickname("updatedTester")
                .build();

        user = User.builder()
                .userId(1L)
                .userEmail("user@example.com")
                .userPwd("password")
                .userNickname("tester")
                .userStatus(Status.ACTIVE)
                .build();
    }

    // 사용자 정보 저장 관련 테스트
    @Test
    void createUser_whenEmailAlreadyExists_shouldThrowDuplicateException() {
        when(userRepository.existsByUserEmailAndUserStatus(requestDto.getUserEmail(), Status.ACTIVE))
                .thenReturn(true);

        assertThrows(DuplicateException.class, () -> {
            userService.createUser(requestDto);
        });
    }

    @Test
    void createUser_whenNicknameAlreadyExists_shouldThrowDuplicateException() {
        when(userRepository.existsByUserEmailAndUserStatus(requestDto.getUserEmail(), Status.ACTIVE)).thenReturn(false);
        when(userRepository.existsByUserNicknameAndUserStatus(requestDto.getUserNickname(), Status.ACTIVE)).thenReturn(true);

        assertThrows(DuplicateException.class, () -> {
            userService.createUser(requestDto);
        });
    }

    @Test
    void createUser_whenPasswordMismatch_shouldThrowBadRequestException() {
        requestDto = requestDto.toBuilder()
                .confirmPwd("wrongPassword")
                .build();

        assertThrows(BadRequestException.class, () -> {
            userService.createUser(requestDto);
        });
    }

    @Test
    void createUser_whenValidData_shouldCreateUser() {
        // given
        when(userRepository.existsByUserEmailAndUserStatus(requestDto.getUserEmail(), Status.ACTIVE)).thenReturn(false);
        when(userRepository.existsByUserNicknameAndUserStatus(requestDto.getUserNickname(), Status.ACTIVE)).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getUserPwd())).thenReturn("encodedPassword");

        userService.createUser(requestDto);

        verify(userRepository).save(any(User.class));
    }

    // 사용자 정보 수정 관련 테스트
    @Test
    void updateUser_whenNicknameAlreadyExists_shouldThrowDuplicateException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); // 기존 사용자 조회
        when(userRepository.existsByUserNicknameAndUserStatus("updatedTester", Status.ACTIVE))
                .thenReturn(true); // 이미 존재하는 닉네임

        assertThrows(DuplicateException.class, () -> userService.updateUser(1L, 1L, updateDto));
    }

    @Test
    void updateUser_whenPasswordMismatch_shouldThrowBadRequestException() {
        UserUpdateDto mismatchedUpdateDto = updateDto.toBuilder()
                .currentPwd("incorrectOldPassword")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrectOldPassword", user.getUserPwd())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            userService.updateUser(1L, 1L, mismatchedUpdateDto);
        });
    }

    @Test
    void updateUser_whenValidData_shouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUserNicknameAndUserStatus("updatedTester", Status.ACTIVE)).thenReturn(false);
        when(passwordEncoder.matches("oldPassword", user.getUserPwd())).thenReturn(true);  // 비밀번호 일치
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPwd");  // 새 비밀번호 암호화

        userService.updateUser(1L, 1L, updateDto);

        assertEquals("updatedTester", user.getUserNickname());
        verify(userRepository).save(any(User.class));  // save가 호출되었는지 검증
    }

    // 사용자 정보 조회 관련 테스트
    @Test
    void getUserById_whenUserExists_shouldReturnUserResponseDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(user.getUserEmail(), response.getUserEmail());
        assertEquals(user.getUserNickname(), response.getUserNickname());
    }

    @Test
    void getUserById_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

}
