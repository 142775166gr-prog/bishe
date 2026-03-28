package com.example.zhjypt.security;

/**
 * 将 JWT 解析后的用户信息放入 ThreadLocal，供 Controller 读取。
 */
public class JwtContext {
    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private JwtContext() {
    }

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser getCurrentUser() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}

