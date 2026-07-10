package com.fucar.renting.config;

import com.fucar.renting.service.impl.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("account")
    public CustomUserDetails populateAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        if ("anonymousUser".equals(auth.getPrincipal())) return null;
        if (auth.getPrincipal() instanceof CustomUserDetails cud) {
            return cud;
        }
        return null;
    }
}
