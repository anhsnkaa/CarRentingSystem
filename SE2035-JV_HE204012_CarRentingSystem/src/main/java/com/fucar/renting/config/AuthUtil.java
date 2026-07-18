package com.fucar.renting.config;

import jakarta.servlet.http.HttpSession;
import com.fucar.renting.entity.Account;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthUtil {

    public static Account currentAccount() {
        HttpSession session = currentSession();
        if (session == null) return null;
        Object acc = session.getAttribute(AuthInterceptor.ATTR_ACCOUNT);
        return acc instanceof Account ? (Account) acc : null;
    }

    public static String currentRole() {
        HttpSession session = currentSession();
        if (session == null) return null;
        return (String) session.getAttribute("currentRole");
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(currentRole());
    }

    public static boolean isCustomer() {
        return "CUSTOMER".equals(currentRole());
    }

    private static HttpSession currentSession() {
        ServletRequestAttributes attrs = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        return attrs.getRequest().getSession(false);
    }
}