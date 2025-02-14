package com.markmycode.mmc.exception.custom;

import com.markmycode.mmc.exception.ErrorCode;
import lombok.Getter;

// 입력값 중복
@Getter
public class DuplicateException extends RuntimeException{

    private final ErrorCode errorCode;

    public DuplicateException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
