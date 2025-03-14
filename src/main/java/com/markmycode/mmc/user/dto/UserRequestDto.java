package com.markmycode.mmc.user.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String userName;
    private String userEmail;
    private String userPwd;
    private String userNickname;

}
