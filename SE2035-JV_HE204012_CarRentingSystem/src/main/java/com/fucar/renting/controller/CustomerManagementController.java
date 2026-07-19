package com.fucar.renting.controller;

import com.fucar.renting.dto.AdminCreateAccountRequest;
import com.fucar.renting.dto.AdminUpdateAccountRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.exception.EmailAlreadyExistsException;
import com.fucar.renting.service.AccountService;
import com.fucar.renting.service.AdminAccountService;
import com.fucar.renting.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerManagementController {

    private final CustomerService customerService;
    private final AdminAccountService adminAccountService;
    private final AccountService accountService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<Customer> customers = customerService.findAll(keyword, page, size);
        Map<Integer, Long> rentalCounts = new HashMap<>();
        for (Customer c : customers) {
            rentalCounts.put(c.getId(), customerService.hasAnyRental(c.getId()) ? 1L : 0L);
        }
        model.addAttribute("customers", customers);
        model.addAttribute("rentalCounts", rentalCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeMenu", "customers");
        return "admin/customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("createAccountRequest")) {
            model.addAttribute("createAccountRequest", new AdminCreateAccountRequest());
        }
        model.addAttribute("activeMenu", "customers");
        return "admin/customers/create";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("createAccountRequest") AdminCreateAccountRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/create";
        }
        try {
            adminAccountService.createCustomerAccount(request);
            ra.addFlashAttribute("toastMessage", "Account created successfully");
            ra.addFlashAttribute("toastType", "success");
        } catch (EmailAlreadyExistsException e) {
            binding.rejectValue("email", "email.exists", "Email already registered");
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/create";
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() == null ? "Failed to create account" : e.getMessage();
            String lower = msg.toLowerCase();
            if (lower.contains("account name")) {
                binding.rejectValue("accountName", "accountName.exists", msg);
            } else if (lower.contains("mobile")) {
                binding.rejectValue("mobile", "mobile.exists", msg);
            } else if (lower.contains("identity")) {
                binding.rejectValue("identityCard", "identityCard.exists", msg);
            } else if (lower.contains("licence")) {
                binding.rejectValue("licenceNumber", "licenceNumber.exists", msg);
            } else {
                model.addAttribute("error", msg);
            }
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/create";
        }
        return "redirect:/admin/customers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Customer c = customerService.findById(id);
        if (c == null) {
            ra.addFlashAttribute("toastMessage", "Customer not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/customers";
        }
        Account a = accountService.findById(c.getAccountId());
        if (a == null) {
            ra.addFlashAttribute("toastMessage", "Associated account not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/customers";
        }
        AdminUpdateAccountRequest request = toUpdateRequest(c, a);
        model.addAttribute("updateRequest", request);
        model.addAttribute("customerId", id);
        model.addAttribute("accountId", a.getId());
        model.addAttribute("activeMenu", "customers");
        return "admin/customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer accountId,
                         @Valid @ModelAttribute("updateRequest") AdminUpdateAccountRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("customerId", id);
            model.addAttribute("accountId", accountId);
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/form";
        }
        try {
            adminAccountService.updateCustomerAndAccount(id, accountId, request);
            ra.addFlashAttribute("toastMessage", "Account updated");
            ra.addFlashAttribute("toastType", "success");
        } catch (EmailAlreadyExistsException e) {
            binding.rejectValue("email", "email.exists", "Email already registered");
            model.addAttribute("customerId", id);
            model.addAttribute("accountId", accountId);
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/form";
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() == null ? "Update failed" : e.getMessage();
            String lower = msg.toLowerCase();
            if (lower.contains("account name")) {
                binding.rejectValue("accountName", "accountName.exists", msg);
            } else if (lower.contains("mobile")) {
                binding.rejectValue("mobile", "mobile.exists", msg);
            } else if (lower.contains("identity")) {
                binding.rejectValue("identityCard", "identityCard.exists", msg);
            } else if (lower.contains("licence")) {
                binding.rejectValue("licenceNumber", "licenceNumber.exists", msg);
            } else {
                ra.addFlashAttribute("toastMessage", msg);
                ra.addFlashAttribute("toastType", "error");
                return "redirect:/admin/customers";
            }
            model.addAttribute("customerId", id);
            model.addAttribute("accountId", accountId);
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/form";
        }
        return "redirect:/admin/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("toastMessage", "Customer deleted");
            ra.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("toastMessage", "Cannot delete customer: " + e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/customers";
    }

    private AdminUpdateAccountRequest toUpdateRequest(Customer c, Account a) {
        return AdminUpdateAccountRequest.builder()
                .accountName(a.getAccountName())
                .email(a.getEmail())
                .password("")
                .fullName(c.getFullName())
                .mobile(c.getMobile())
                .birthday(c.getBirthday())
                .identityCard(c.getIdentityCard())
                .licenceNumber(c.getLicenceNumber())
                .licenceDate(c.getLicenceDate())
                .build();
    }
}