package com.markmycode.mmc.exception.custom;

import com.markmycode.mmc.exception.ErrorCode;
import lombok.Getter;

// 리소스 없음
@Getter
public class NotFoundException extends RuntimeException{

    private final ErrorCode errorCode;

    // ErrorCode를 받아 해당 에러 메시지를 RuntimeException에 전달
    public NotFoundException(ErrorCode errorCode){
        super(errorCode.getMessage()); // 부모 클래스인 RuntimeException의 생성자에 메시지를 전달함
        this.errorCode = errorCode;
    }

}
