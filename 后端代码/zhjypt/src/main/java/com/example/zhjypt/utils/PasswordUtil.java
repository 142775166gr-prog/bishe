package com.example.zhjypt.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类：统一使用 BCrypt（兼容历史明文密码）。
 */
public class PasswordUtil {

    // BCryptPasswordEncoder 默认 strength=10（足够安全，且性能开销可接受）
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    /**
     * 将明文密码哈希为 BCrypt；如果传入的本身已经是 BCrypt，则原样返回。
     */
    public static String hashIfNeeded(String rawOrHash) {
        if (rawOrHash == null) return null;
        if (isBCryptHash(rawOrHash)) {
            return rawOrHash;
        }
        return ENCODER.encode(rawOrHash);
    }

    /**
     * 校验：兼容旧的明文密码（当数据库里存的是明文时也能比对）。
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) return false;
        if (isBCryptHash(storedPassword)) {
            return ENCODER.matches(rawPassword, storedPassword);
        }
        // legacy 明文：直接比较
        return storedPassword.equals(rawPassword);
    }

    /**
     * 判断字符串是否是 BCrypt 哈希格式。
     */
    public static boolean isBCryptHash(String value) {
        if (value == null) return false;
        // BCrypt 哈希一般形如 $2a$ / $2b$ / $2y$ ...
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$") || value.startsWith("$2$");
    }
}

