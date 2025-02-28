package com.markmycode.mmc.exception.custom;

import com.markmycode.mmc.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{

    private final ErrorCode errorCode;

    public BadRequestException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
