package com.fucar.renting.controller;

import com.fucar.renting.dto.CustomerUpdateRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fucar.renting.service.impl.CustomUserDetails;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "customer/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        if (principal == null) return "redirect:/login";
        Account account = principal.getAccount();
        Customer customer = customerService.findByAccountId(account.getId());
        if (customer == null) {
            return "redirect:/customer/profile/new";
        }
        model.addAttribute("customerRequest", toRequest(customer));
        model.addAttribute("activeMenu", "profile");
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                @Valid @ModelAttribute("customerRequest") CustomerUpdateRequest request,
                                BindingResult binding,
                                Model model,
                                RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(principal.getAccount().getId());
        if (customer == null) {
            ra.addFlashAttribute("toastMessage", "Profile not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/profile";
        }
        if (binding.hasErrors()) {
            model.addAttribute("activeMenu", "profile");
            return "customer/profile";
        }
        try {
            customerService.update(customer.getId(), request);
            ra.addFlashAttribute("toastMessage", "Profile updated");
            ra.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("toastMessage", "Update failed: " + e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/customer/profile";
    }

    private CustomerUpdateRequest toRequest(Customer customer) {
        return CustomerUpdateRequest.builder()
                .fullName(customer.getFullName())
                .mobile(customer.getMobile())
                .birthday(customer.getBirthday())
                .identityCard(customer.getIdentityCard())
                .licenceNumber(customer.getLicenceNumber())
                .licenceDate(customer.getLicenceDate())
                .build();
    }
}