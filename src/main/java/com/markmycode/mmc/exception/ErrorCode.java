package com.markmycode.mmc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 예외 상황에 대한 HTTP 상태 코드와 기본 메시지 정의
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // NotFoundException
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    // DuplicateException
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,  "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    //UnauthorizedException (클라이언트가 인증되지 않은 상태에서 인증을 요구하는 리소스에 접근하려고 할 때)
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    // ForbiddenException (클라이언트가 인증은 되어 있지만 접근 권한이 없는 리소스에 접근하려 할 때)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    EMAIL_MISMATCH(HttpStatus.FORBIDDEN, "제공된 이메일이 로그인한 사용자와 일치하지 않습니다."),

    // ServerException
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status; // HTTP 상태 코드 (HttpStatus 객체)
    private final String message; // 에러에 대한 기본 메세지

    // HttpStatus 객체에서 상태 코드(정수 값)를 추출하여 반환
    public int value(){
        return this.status.value();
    }
}
