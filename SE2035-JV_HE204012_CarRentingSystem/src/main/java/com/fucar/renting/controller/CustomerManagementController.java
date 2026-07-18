package com.fucar.renting.controller;

import com.fucar.renting.dto.CustomerUpdateRequest;
import com.fucar.renting.entity.Customer;
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

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Customer c = customerService.findById(id);
        if (c == null) {
            ra.addFlashAttribute("toastMessage", "Customer not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/customers";
        }
        model.addAttribute("customerRequest", toRequest(c));
        model.addAttribute("customerId", id);
        model.addAttribute("activeMenu", "customers");
        return "admin/customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("customerRequest") CustomerUpdateRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("customerId", id);
            model.addAttribute("activeMenu", "customers");
            return "admin/customers/form";
        }
        try {
            customerService.update(id, request);
            ra.addFlashAttribute("toastMessage", "Customer updated");
            ra.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("toastMessage", "Update failed: " + e.getMessage());
            ra.addFlashAttribute("toastType", "error");
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

    private CustomerUpdateRequest toRequest(Customer c) {
        return CustomerUpdateRequest.builder()
                .fullName(c.getFullName())
                .mobile(c.getMobile())
                .birthday(c.getBirthday())
                .identityCard(c.getIdentityCard())
                .licenceNumber(c.getLicenceNumber())
                .licenceDate(c.getLicenceDate())
                .build();
    }
}