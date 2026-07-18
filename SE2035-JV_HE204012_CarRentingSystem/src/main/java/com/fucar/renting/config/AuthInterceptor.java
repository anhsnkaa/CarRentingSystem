package com.fucar.renting.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_ACCOUNT = "currentAccount";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relative = path.substring(contextPath.length());

        if (isPublicPath(relative)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object account = session == null ? null : session.getAttribute(ATTR_ACCOUNT);

        if (account == null) {
            response.sendRedirect(contextPath + "/login");
            return false;
        }

        String role = (String) session.getAttribute("currentRole");
        if (relative.startsWith("/admin/") && !"ADMIN".equals(role)) {
            response.sendRedirect(contextPath + "/");
            return false;
        }
        if (relative.startsWith("/customer/") && !"CUSTOMER".equals(role)) {
            response.sendRedirect(contextPath + "/");
            return false;
        }

        return true;
    }

    private boolean isPublicPath(String path) {
        return path.equals("/")
                || path.equals("/home")
                || path.startsWith("/login")
                || path.startsWith("/logout")
                || path.startsWith("/register")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/webjars/")
                || path.startsWith("/error")
                || path.startsWith("/images/")
                || path.equals("/favicon.ico");
    }
}