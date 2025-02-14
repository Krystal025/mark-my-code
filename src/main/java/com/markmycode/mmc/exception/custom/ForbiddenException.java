package com.markmycode.mmc.exception.custom;

import com.markmycode.mmc.exception.ErrorCode;
import lombok.Getter;

// 권한 없음
@Getter
public class ForbiddenException extends RuntimeException{

    private final ErrorCode errorCode;

    public ForbiddenException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
