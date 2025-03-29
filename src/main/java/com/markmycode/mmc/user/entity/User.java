package com.markmycode.mmc.user.entity;

import com.markmycode.mmc.user.dto.UserRequestDto;
import com.markmycode.mmc.user.dto.UserResponseDto;
import com.markmycode.mmc.user.enums.Provider;
import com.markmycode.mmc.user.enums.Role;
import com.markmycode.mmc.user.enums.Status;
import com.markmycode.mmc.util.EmailUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // 기존 인스턴스를 복사하여 새로운 인스턴스를 만들고, 일부 필드만 변경할 수 있도록 함
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column
    private String userPwd;

    @Column(nullable = false, unique = true)
    private String userNickname;

    @Enumerated(EnumType.STRING)
    private Status userStatus;

    @Column(updatable = false)
    private LocalDateTime userCreatedAt;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    @Column(length = 100, unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    private Provider socialProvider;

    @PrePersist // 처음 생성되는 엔티티가 DB에 저장되기 전에 호출
    private void onCreate(){
        if(this.userStatus == null){
            this.userStatus = Status.ACTIVE;
        }
        if(this.userRole == null){
            this.userRole = Role.ROLE_USER;
        }
        this.userCreatedAt = LocalDateTime.now();
    }

    public static User fromDto(UserRequestDto dto, String encodedPassword) {
        return User.builder()
                .userName(dto.getUserName())
                .userEmail(EmailUtils.normalizeEmail(dto.getUserEmail()))
                .userPwd(encodedPassword)
                .userNickname(dto.getUserNickname())
                .build();
    }

    public UserResponseDto toResponseDto() {
        return UserResponseDto.builder()
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .userNickname(userNickname)
                .userStatus(userStatus)
                .userCreatedAt(userCreatedAt)
                .build();
    }

    // 사용자 정보 변경을 위한 도메인 메서드 (Setter 직접 노출 방지)
    public void updateNickname(String nickname){
        this.userNickname = nickname;
    }

    public void updatePwd(String password){
        this.userPwd = password;

    }

    public void deactivate(){
        this.userStatus = Status.INACTIVE;
    }

}
