package com.markmycode.mmc.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status; // HTTP 상태 코드 (HttpStatus 객체로 보관하여 상태코드와 설명을 함께 관리)
    private String code; // 에러 코드 문자열 (보통 ErrorCode 이름 사용)
    private String message; // 클라이언트에게 전달할 에러 메시지

    // ErrorCode에 정의된 정보를 바탕으로 ErrorResponse 객체 생성
    public static ErrorResponse of(ErrorCode errorCode){
        return ErrorResponse.builder()
                .status(errorCode.getStatus()) // HttpStatus 객체 반환
                .code(errorCode.name()) // Enum의 이름을 코드로 사용
                .message(errorCode.getMessage()) // ErrorCode에서 정의한 메시지 사용
                .build();
    }
}
