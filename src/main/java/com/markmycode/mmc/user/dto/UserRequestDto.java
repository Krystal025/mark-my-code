package com.markmycode.mmc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String userName;
    private String userEmail;
    private String userPwd;
    private String userNickname;

}
