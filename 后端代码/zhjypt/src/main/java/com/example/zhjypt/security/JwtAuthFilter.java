package com.example.zhjypt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 鉴权过滤器：解析 token 并把用户信息放到 JwtContext。
 *
 * 注意：这里是“止血最小实现”，Controller 侧会继续做角色/归属校验。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // CORS 预检请求必须直接放行，否则前端带 Authorization 会被浏览器拦截
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 登录接口与注册接口跳过鉴权（不需要 token）
        if (uri.contains("/Student/StudentLogin")
                || uri.contains("/Teacher/TeacherLogin")
                || uri.contains("/Admin/AdminLogin")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring("Bearer ".length());
                CurrentUser user = jwtUtil.parseToken(token);
                if (user != null) {
                    JwtContext.set(user);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            JwtContext.clear();
        }
    }
}

