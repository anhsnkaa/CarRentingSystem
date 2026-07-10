package com.fucar.renting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return ("customer/dashboard");
    }

    @GetMapping("/profile")
    public String profile() {
        return "customer/profile";
    }
}
