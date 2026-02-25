package com.code808.calmdesk.domain.businesscard.port.impl;

import java.util.regex.Pattern;

/**
 * 명함 추출 결과 값 후처리: 레이블 제거 + 플레이스홀더/예시 값 제거.
 */
public final class BusinessCardValueNormalizer {

    /** AI가 넣은 예시 전화번호 → null 처리 */
    private static final Pattern PLACEHOLDER_PHONE = Pattern.compile("^010-0000-0000$");
    /** example.com 등 예시 이메일 → null 처리 */
    private static final Pattern PLACEHOLDER_EMAIL = Pattern.compile("example\\.(com|org|net)|가나다@|test@");
    /** 직책/회사 설명을 이름으로 넣은 경우 → null 처리 (이름란에 넣지 말았어야 할 값) */
    private static final String[] NAME_PLACEHOLDER_PHRASES = {
            "한국의 대표", "대표이사", "직책", "회사명", "이름"
    };

    private static final Pattern PHONE_PREFIX = Pattern.compile(
            "^(Phone|Tel|T\\.|Mobile|M\\.|전화|휴대|연락처|Fax|F\\.|팩스)\\s*[:.]?\\s*",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PREFIX = Pattern.compile(
            "^(Email|E-mail|E\\.mail|이메일|메일)\\s*[:.]?\\s*",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern ADDRESS_PREFIX = Pattern.compile(
            "^(Address|Addr|주소|사무실)\\s*[:.]?\\s*",
            Pattern.CASE_INSENSITIVE);

    /**
     * 필드 키에 따라 접두어(레이블) 제거 후 trim. 이름은 공백만 정리.
     */
    public static String normalize(String key, String value) {
        if (value == null || value.isBlank()) return null;
        String s = value.trim();
        if (s.isEmpty()) return null;
        switch (key) {
            case "phone":
            case "mobile":
            case "fax":
                s = PHONE_PREFIX.matcher(s).replaceFirst("");
                s = cleanPhone(s);
                return isPlaceholderPhone(s) ? null : s;
            case "email":
                s = EMAIL_PREFIX.matcher(s).replaceFirst("");
                s = s.trim();
                return (s.isEmpty() || isPlaceholderEmail(s)) ? null : s;
            case "address":
                s = ADDRESS_PREFIX.matcher(s).replaceFirst("");
                return s.trim().isEmpty() ? null : s.trim();
            case "name":
                s = cleanName(s);
                return isPlaceholderName(s) ? null : s;
            default:
                return s;
        }
    }

    private static boolean isPlaceholderPhone(String s) {
        return s != null && PLACEHOLDER_PHONE.matcher(s.trim()).matches();
    }

    private static boolean isPlaceholderEmail(String s) {
        return s != null && PLACEHOLDER_EMAIL.matcher(s).find();
    }

    private static boolean isPlaceholderName(String s) {
        if (s == null || s.isBlank()) return true;
        String t = s.trim();
        for (String phrase : NAME_PLACEHOLDER_PHRASES) {
            if (phrase.equals(t) || t.equals(phrase)) return true;
        }
        return false;
    }

    private static String cleanPhone(String s) {
        if (s == null || s.isBlank()) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static String cleanName(String s) {
        if (s == null || s.isBlank()) return null;
        return s.replaceAll("\\s+", " ").trim();
    }

    private BusinessCardValueNormalizer() {}
}
