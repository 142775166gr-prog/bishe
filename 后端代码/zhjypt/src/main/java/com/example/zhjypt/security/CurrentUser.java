package com.example.zhjypt.security;

/**
 * 当前登录用户信息（从 JWT 中解析得到）。
 */
public class CurrentUser {
    private final String role; // admin / teacher / student
    private final Integer uid; // 对应 role 的用户主键：adm_id/teach_id/stu_id

    public CurrentUser(String role, Integer uid) {
        this.role = role;
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public Integer getUid() {
        return uid;
    }
}

