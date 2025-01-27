package com.markmycode.mmc.user.dto;

import com.markmycode.mmc.user.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {

    private Long userId;
    private String userName;
    private String userEmail;
    private String userNickname;
    private Status userStatus;
    private LocalDateTime userCreatedAt;

}
