package com.fucar.renting.controller;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.exception.EmailAlreadyExistsException;
import com.fucar.renting.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
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
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            registerService.register(request);
        } catch (EmailAlreadyExistsException e) {
            model.addAttribute("error", "Email already registered!");
            return "auth/register";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}
