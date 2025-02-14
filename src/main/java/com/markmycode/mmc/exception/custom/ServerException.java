package com.markmycode.mmc.exception.custom;

import com.markmycode.mmc.exception.ErrorCode;
import lombok.Getter;

// 서버 오류
@Getter
public class ServerException extends RuntimeException{

    private final ErrorCode errorCode;

    public ServerException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
