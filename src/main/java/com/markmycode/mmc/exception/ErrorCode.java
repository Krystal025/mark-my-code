package com.markmycode.mmc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 예외 상황에 대한 HTTP 상태 코드와 기본 메시지 정의
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST: 잘못된 요청
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일이나 비밀번호입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_COMMENT(HttpStatus.BAD_REQUEST, "해당 게시글에 속한 댓글이 아닙니다."),
    INVALID_PARENT_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 상위 카테고리입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다."),
    INVALID_PLATFORM(HttpStatus.BAD_REQUEST, "유효하지 않은 플랫폼입니다."),
    INVALID_LANGUAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 언어입니다."),

    // 401 UNAUTHORIZED: 인증 실패
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    ACCESS_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),

    // 403 FORBIDDEN: 권한 부족
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 리소스에 대한 접근 권한이 없습니다."),
    USER_NOT_MATCH(HttpStatus.FORBIDDEN, "현재 사용자와 요청 정보가 일치하지 않습니다."),
    CANNOT_LIKE_OWN_POST(HttpStatus.FORBIDDEN, "본인의 게시글에는 좋아요를 누를 수 없습니다."),
    CANNOT_UNLIKE(HttpStatus.FORBIDDEN, "본인이 누른 좋아요만 취소할 수 있습니다."),

    // 404 NOT_FOUND: 리소스 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자 정보를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, "조건에 맞는 게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 댓글을 찾을 수 없습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 기록이 존재하지 않습니다."),

    // 409 CONFLICT: 리소스 충돌
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,  "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    LIKE_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 좋아요를 등록한 상태입니다."),

    // 500 INTERNAL_SERVER_ERROR: 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status; // HTTP 상태 코드 (HttpStatus 객체)
    private final String message; // 에러에 대한 기본 메세지

    // HttpStatus 객체의 숫자 상태코드 반환
    public int getStatusCode(){
        return status.value();
    }

}
