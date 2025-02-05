package com.markmycode.mmc.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2RequestDto {
    private String userName;
    private String userEmail;
    private String userNickname;
}
