package com.markmycode.mmc.util;

public class EmailUtils {

    public static String normalizeEmail(String email) {
        if (email == null) return null;
        // 이메일을 소문자로 변환하고 양쪽 공백 제거
        email = email.trim().toLowerCase();
        // 추가적인 이메일 정규화 로직 (예: '.' 제거, '+' 처리 등)을 여기에 구현
        return email;
    }

}
