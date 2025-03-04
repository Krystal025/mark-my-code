package com.markmycode.mmc.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {

    private String userName;
    private String userEmail;
    private String userPwd;
    private String userNickname;

}
