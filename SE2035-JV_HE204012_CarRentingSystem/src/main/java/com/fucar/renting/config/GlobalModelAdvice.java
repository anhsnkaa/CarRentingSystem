package com.fucar.renting.config;

import com.fucar.renting.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("account")
    public Account populateAccount() {
        return AuthUtil.currentAccount();
    }

    @ModelAttribute(CsrfInterceptor.MODEL_ATTR)
    public CsrfToken csrfToken(HttpServletRequest request) {
        Object token = request.getAttribute(CsrfInterceptor.MODEL_ATTR);
        if (token == null) return null;
        return new CsrfToken((String) token);
    }

    public static class CsrfToken {
        private final String token;
        public CsrfToken(String token) { this.token = token; }
        public String getParameterName() { return CsrfInterceptor.PARAM_NAME; }
        public String getToken() { return token; }
    }
}