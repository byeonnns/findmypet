package com.findmypet.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    private static final int LOG_ROUNDS = 12;

    public static String hash(String rawPassword) {
        // salt 생성: cost 값(LOG_ROUNDS) 포함
        String salt = BCrypt.gensalt(LOG_ROUNDS);
        // rawPassword + salt → 해시 문자열
        return BCrypt.hashpw(rawPassword, salt);
    }

    public static boolean verify(String rawPassword, String storedHash) {
        return BCrypt.checkpw(rawPassword, storedHash);
    }
}
