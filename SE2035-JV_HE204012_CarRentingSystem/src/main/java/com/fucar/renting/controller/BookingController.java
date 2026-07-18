package com.fucar.renting.controller;

import com.fucar.renting.dto.BookingRequest;
import com.fucar.renting.dto.CarRentalRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;
import com.fucar.renting.service.CarRentalService;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.CustomerService;
import com.fucar.renting.service.ReviewService;
import com.fucar.renting.service.impl.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final CarRentalService rentalService;
    private final CarService carService;
    private final CustomerService customerService;
    private final ReviewService reviewService;

    @GetMapping
    public String history(@AuthenticationPrincipal CustomUserDetails principal,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
        if (principal == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(principal.getAccount().getId());
        if (customer == null) {
            model.addAttribute("rentals", Page.empty());
            model.addAttribute("activeMenu", "bookings");
            return "customer/bookings/list";
        }
        Page<CarRental> rentals = rentalService.findByCustomer(customer.getId(), page, size);
        model.addAttribute("rentals", rentals);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentals.getTotalPages());
        model.addAttribute("activeMenu", "bookings");
        return "customer/bookings/list";
    }

    @GetMapping("/new")
    public String newBookingForm(Model model) {
        List<Car> availableCars = carService.search(null, "Available", 1, 100).getContent();
        model.addAttribute("cars", availableCars);
        model.addAttribute("bookingRequest",
                BookingRequest.builder()
                        .pickupDate(LocalDate.now())
                        .returnDate(LocalDate.now().plusDays(1))
                        .build());
        model.addAttribute("activeMenu", "bookings");
        return "customer/bookings/new";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails principal,
                         @Valid @ModelAttribute("bookingRequest") BookingRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(principal.getAccount().getId());
        if (customer == null) {
            ra.addFlashAttribute("toastMessage", "Please complete your profile first");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/profile";
        }
        if (binding.hasErrors()) {
            model.addAttribute("cars", carService.search(null, "Available", 1, 100).getContent());
            return "customer/bookings/new";
        }
        try {
            CarRentalRequest rentalReq = CarRentalRequest.builder()
                    .carIds(request.getCarIds())
                    .pickupDate(request.getPickupDate())
                    .returnDate(request.getReturnDate())
                    .status("Active")
                    .build();
            rentalService.createRentals(customer.getId(), rentalReq);
            ra.addFlashAttribute("toastMessage", "Booking created successfully");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings/new";
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal CustomUserDetails principal,
                         @PathVariable Integer id,
                         Model model,
                         RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";
        CarRental rental = rentalService.findById(id);
        if (rental == null) {
            ra.addFlashAttribute("toastMessage", "Booking not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings";
        }
        Customer customer = customerService.findByAccountId(principal.getAccount().getId());
        if (customer == null || !rental.getCustomerId().equals(customer.getId())) {
            ra.addFlashAttribute("toastMessage", "Not authorized");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings";
        }
        Car car = carService.findById(rental.getCarId());
        List<Review> reviews = reviewService.findByCarRental(id);
        model.addAttribute("rental", rental);
        model.addAttribute("car", car);
        model.addAttribute("reviews", reviews);
        model.addAttribute("activeMenu", "bookings");
        return "customer/bookings/detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@AuthenticationPrincipal CustomUserDetails principal,
                         @PathVariable Integer id,
                         RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";
        CarRental rental = rentalService.findById(id);
        if (rental == null) {
            ra.addFlashAttribute("toastMessage", "Booking not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings";
        }
        Customer customer = customerService.findByAccountId(principal.getAccount().getId());
        if (customer == null || !rental.getCustomerId().equals(customer.getId())) {
            ra.addFlashAttribute("toastMessage", "Not authorized");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings";
        }
        rentalService.delete(id);
        ra.addFlashAttribute("toastMessage", "Booking cancelled");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/customer/bookings";
    }

    @PostMapping("/{id}/review")
    public String review(@AuthenticationPrincipal CustomUserDetails principal,
                         @PathVariable Integer id,
                         @RequestParam Integer stars,
                         @RequestParam String comment,
                         RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";
        if (stars == null || stars < 1 || stars > 5) {
            ra.addFlashAttribute("toastMessage", "Rating must be 1-5");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings/" + id;
        }
        try {
            com.fucar.renting.dto.ReviewRequest rr = com.fucar.renting.dto.ReviewRequest.builder()
                    .carRenId(id)
                    .reviewStar(stars)
                    .comment(comment)
                    .build();
            reviewService.create(principal.getAccount().getId(), rr);
            ra.addFlashAttribute("toastMessage", "Review submitted");
            ra.addFlashAttribute("toastType", "success");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/customer/bookings/" + id;
    }
}