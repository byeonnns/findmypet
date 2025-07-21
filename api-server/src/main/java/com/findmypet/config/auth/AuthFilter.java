package com.findmypet.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1) // 필터 순서 배치 -> 지금보다 뒤에 있으면 세션이 null로 들어옴
public class AuthFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 1) 프리플라이트 요청은 무조건 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        return path.startsWith("/api/users/register")
                || path.startsWith("/api/users/login")
                || path.startsWith("/api/users/logout")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String rawCookie = request.getHeader("Cookie");
        log.info("[AuthFilter] Cookie 헤더 원본: {}", rawCookie); // 세션 ID 확인

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("[AuthFilter] Cookie name: {}, value: {}", cookie.getName(), cookie.getValue());
            }
        }


        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("[AuthFilter] 세션 없음");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Object userId = session.getAttribute(SESSION_USER_ID);
        log.info("[AuthFilter] 세션 ID: {}, 사용자 ID: {}", session.getId(), userId);

        if (userId == null) {
            log.warn("[AuthFilter] 사용자 ID 없음");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        request.setAttribute(SESSION_USER_ID, userId);
        filterChain.doFilter(request, response);
    }
}
