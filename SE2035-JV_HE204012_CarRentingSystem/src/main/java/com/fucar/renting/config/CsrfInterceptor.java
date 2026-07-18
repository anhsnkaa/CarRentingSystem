package com.fucar.renting.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class CsrfInterceptor implements HandlerInterceptor {

    public static final String SESSION_ATTR = "_csrfToken";
    public static final String PARAM_NAME = "_csrf";
    public static final String MODEL_ATTR = "_csrf";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (isCsrfExempt(path)) {
            return true;
        }

        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(SESSION_ATTR);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(SESSION_ATTR, token);
        }
        request.setAttribute(MODEL_ATTR, token);

        String method = request.getMethod();
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
            String submitted = request.getParameter(PARAM_NAME);
            if (submitted == null || !submitted.equals(token)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
                return false;
            }
        }
        return true;
    }

    private boolean isCsrfExempt(String path) {
        return path.equals("/login")
                || path.equals("/logout")
                || path.equals("/register");
    }
}