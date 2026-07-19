package com.fucar.renting.controller;

import com.fucar.renting.config.AuthInterceptor;
import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.exception.EmailAlreadyExistsException;
import com.fucar.renting.service.AccountService;
import com.fucar.renting.service.RegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;
    private final AccountService accountService;

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute(AuthInterceptor.ATTR_ACCOUNT) != null) {
            String role = (String) session.getAttribute("currentRole");
            if ("ADMIN".equals(role)) return "redirect:/admin/dashboard";
            if ("CUSTOMER".equals(role)) return "redirect:/customer/dashboard";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String accountName,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        Account acc = accountService.findByAccountName(accountName);
        if (acc == null || !acc.getPassword().equals(password)) {
            ra.addFlashAttribute("toastMessage", "Invalid username or password.");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/login?error";
        }
        session.setAttribute(AuthInterceptor.ATTR_ACCOUNT, acc);
        session.setAttribute("currentRole", acc.getRole());
        if ("ADMIN".equals(acc.getRole())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/customer/dashboard";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new CustomerRegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerRequest") CustomerRegisterRequest request,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            registerService.register(request);
            ra.addFlashAttribute("toastMessage", "Registration successful. Please sign in.");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() == null ? "Failed to register" : e.getMessage();
            String lower = msg.toLowerCase();
            if (lower.contains("account name")) {
                bindingResult.rejectValue("accountName", "accountName.exists", msg);
            } else if (lower.contains("mobile")) {
                bindingResult.rejectValue("mobile", "mobile.exists", msg);
            } else if (lower.contains("identity")) {
                bindingResult.rejectValue("identityCard", "identityCard.exists", msg);
            } else if (lower.contains("licence")) {
                bindingResult.rejectValue("licenceNumber", "licenceNumber.exists", msg);
            } else {
                model.addAttribute("error", msg);
            }
            return "auth/register";
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "email.exists", "Email already registered!");
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}