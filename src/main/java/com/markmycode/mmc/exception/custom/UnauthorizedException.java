package com.markmycode.mmc.exception.custom;

import com.markmycode.mmc.exception.ErrorCode;
import lombok.Getter;

// 인증 실패
@Getter
public class UnauthorizedException extends RuntimeException{

    private final ErrorCode errorCode;

    public UnauthorizedException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
