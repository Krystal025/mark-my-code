package com.markmycode.mmc.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

public class CookieUtils {

    public static void addCookie(HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false); // true : Javascript 접근 차단
        // cookie.setSecure(true); // HTTPS 환경에서만 활성화 (개발/테스트 시에는 주석 처리)
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 1일 유효
        cookie.setAttribute("SameSite", "Strict"); // SameSite 설정
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

}
