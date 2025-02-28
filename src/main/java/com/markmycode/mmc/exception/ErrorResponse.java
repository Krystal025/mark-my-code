package com.markmycode.mmc.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private int status; // HTTP 숫자 상태코드
    private String code; // 에러 코드 (ErrorCode Enum 이름)
    private String message; // 에러 메시지

    // ErrorCode에 정의된 정보를 바탕으로 ErrorResponse 객체 생성
    public static ErrorResponse of(ErrorCode errorCode){
        return ErrorResponse.builder()
                .status(errorCode.getStatusCode())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }
}
