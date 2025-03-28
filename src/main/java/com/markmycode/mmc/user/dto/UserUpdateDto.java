package com.markmycode.mmc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private String currentPwd;
    private String userPwd;
    private String confirmPwd;
    private String userNickname;
}
