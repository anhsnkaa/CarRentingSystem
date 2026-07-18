package com.fucar.renting.controller;

import com.fucar.renting.dto.CarRentalRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.service.CarRentalService;
import com.fucar.renting.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class CarRentalManagementController {

    private final CarRentalService rentalService;
    private final CarService carService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String status,
                       @RequestParam(required = false) LocalDate from,
                       @RequestParam(required = false) LocalDate to,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<CarRental> rentals = rentalService.search(status, from, to, page, size);
        model.addAttribute("rentals", rentals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentals.getTotalPages());
        model.addAttribute("status", status);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("activeMenu", "rentals");
        return "admin/rentals/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        CarRental rental = rentalService.findById(id);
        if (rental == null) {
            ra.addFlashAttribute("toastMessage", "Rental not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/rentals";
        }
        Car car = carService.findById(rental.getCarId());
        model.addAttribute("rental", rental);
        model.addAttribute("car", car);
        model.addAttribute("activeMenu", "rentals");
        return "admin/rentals/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        CarRental r = rentalService.findById(id);
        if (r == null) {
            ra.addFlashAttribute("toastMessage", "Rental not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/rentals";
        }
        model.addAttribute("rentalRequest", toRequest(r));
        model.addAttribute("rentalId", id);
        model.addAttribute("activeMenu", "rentals");
        return "admin/rentals/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("rentalRequest") CarRentalRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("rentalId", id);
            model.addAttribute("activeMenu", "rentals");
            return "admin/rentals/form";
        }
        try {
            rentalService.update(id, request);
            ra.addFlashAttribute("toastMessage", "Rental updated");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/rentals";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        rentalService.delete(id);
        ra.addFlashAttribute("toastMessage", "Rental deleted");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/admin/rentals";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Integer id, RedirectAttributes ra) {
        rentalService.complete(id);
        ra.addFlashAttribute("toastMessage", "Rental marked as completed. Car is now Available.");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/admin/rentals";
    }

    private CarRentalRequest toRequest(CarRental r) {
        return CarRentalRequest.builder()
                .carIds(java.util.List.of(r.getCarId()))
                .pickupDate(r.getPickupDate())
                .returnDate(r.getReturnDate())
                .rentPrice(r.getRentPrice())
                .status(r.getStatus())
                .build();
    }
}