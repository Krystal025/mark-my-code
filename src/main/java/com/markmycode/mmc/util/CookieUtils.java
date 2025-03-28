package com.markmycode.mmc.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

public class CookieUtils {

    public static void addCookie(HttpServletResponse response, String name, String value, int expiration){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(expiration); // 쿠키 만료시간 (토큰 만료시간과 일치시킴)
        cookie.setAttribute("SameSite", "Lax"); // SameSite 설정
        cookie.setHttpOnly(true); // true : Javascript 접근 차단
        // cookie.setSecure(true); // HTTPS 환경에서만 활성화 (개발/테스트 시에는 주석 처리)
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name){
        if(request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public static void deleteCookie(HttpServletResponse response, String name){
        // 쿠키 만료 처리
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");  // 쿠키의 경로 설정
        cookie.setMaxAge(0);  // 쿠키의 만료 시간을 0으로 설정하여 즉시 삭제
        cookie.setHttpOnly(true);  // 안전성을 위해 HttpOnly 설정
        cookie.setAttribute("SameSite", "Strict");  // SameSite 설정
        response.addCookie(cookie);  // 응답에 쿠키 추가
    }
}
