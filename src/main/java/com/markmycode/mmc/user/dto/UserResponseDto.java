package com.markmycode.mmc.user.dto;

import com.markmycode.mmc.user.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long userId;
    private String userName;
    private String userEmail;
    private String userNickname;
    private Status userStatus;
    private LocalDateTime userCreatedAt;

}
