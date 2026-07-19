package com.fucar.renting.controller;

import com.fucar.renting.dto.CustomerUpdateRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.service.CustomerService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String profile(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("currentAccount");
        if (account == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(account.getId());
        if (customer == null) {
            return "redirect:/customer/profile/new";
        }
        model.addAttribute("customerRequest", toRequest(customer));
        model.addAttribute("activeMenu", "profile");
        return "customer/profile";
    }

    @GetMapping("/profile/new")
    public String newProfileForm(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("currentAccount");
        if (account == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(account.getId());
        if (customer != null) {
            return "redirect:/customer/profile";
        }
        model.addAttribute("customerRequest", CustomerUpdateRequest.builder().build());
        model.addAttribute("activeMenu", "profile");
        model.addAttribute("isNew", true);
        return "customer/profile";
    }

    @PostMapping("/profile/new")
    public String createProfile(@Valid @ModelAttribute("customerRequest") CustomerUpdateRequest request,
                                BindingResult binding,
                                Model model,
                                RedirectAttributes ra,
                                HttpSession session) {
        Account account = (Account) session.getAttribute("currentAccount");
        if (account == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(account.getId());
        if (customer != null) {
            return "redirect:/customer/profile";
        }
        if (binding.hasErrors()) {
            model.addAttribute("activeMenu", "profile");
            model.addAttribute("isNew", true);
            return "customer/profile";
        }
        try {
            com.fucar.renting.dto.CustomerRegisterRequest reg = com.fucar.renting.dto.CustomerRegisterRequest.builder()
                    .fullName(request.getFullName())
                    .mobile(request.getMobile())
                    .birthday(request.getBirthday())
                    .identityCard(request.getIdentityCard())
                    .licenceNumber(request.getLicenceNumber())
                    .licenceDate(request.getLicenceDate())
                    .build();
            customerService.createForAccount(account.getId(), reg);
            ra.addFlashAttribute("toastMessage", "Profile created");
            ra.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("toastMessage", "Create failed: " + e.getMessage());
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/profile/new";
        }
        return "redirect:/customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("customerRequest") CustomerUpdateRequest request,
                                BindingResult binding,
                                Model model,
                                RedirectAttributes ra,
                                HttpSession session) {
        Account account = (Account) session.getAttribute("currentAccount");
        if (account == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(account.getId());
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