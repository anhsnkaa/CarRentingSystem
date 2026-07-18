package com.fucar.renting.controller;

import com.fucar.renting.dto.BookingRequest;
import com.fucar.renting.dto.CarRentalRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;
import com.fucar.renting.service.CarProducerService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final CarRentalService rentalService;
    private final CarService carService;
    private final CarProducerService producerService;
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
        List<Car> allCars = carService.search(null, "", 1, 100).getContent();
        Map<Integer, CarProducer> producerMap = new HashMap<>();
        for (Car c : allCars) {
            if (c.getProducerId() != null) {
                producerMap.computeIfAbsent(c.getProducerId(),
                        id -> producerService.findById(id));
            }
        }
        model.addAttribute("cars", allCars);
        model.addAttribute("producerMap", producerMap);
        model.addAttribute("activeMenu", "bookings");
        return "customer/bookings/new";
    }

    @GetMapping("/cars/{carId}")
    public String carDetail(@PathVariable Integer carId, Model model, RedirectAttributes ra) {
        Car car = carService.findById(carId);
        if (car == null) {
            ra.addFlashAttribute("toastMessage", "Car not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings/new";
        }
        CarProducer producer = car.getProducerId() != null
                ? producerService.findById(car.getProducerId())
                : null;
        List<Review> reviews = reviewService.findByCarId(carId);
        java.util.Map<Integer, com.fucar.renting.entity.Customer> reviewCustomerMap = reviewService.buildCustomerMap(reviews);
        double avgRating = reviews.isEmpty() ? 0.0
                : reviews.stream().mapToInt(Review::getReviewStar).average().orElse(0.0);
        BookingRequest bookingRequest = BookingRequest.builder()
                .carIds(List.of(carId))
                .pickupDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(1))
                .build();
        model.addAttribute("car", car);
        model.addAttribute("producer", producer);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCustomerMap", reviewCustomerMap);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("activeMenu", "bookings");
        return "customer/bookings/car-detail";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails principal,
                         @Valid @ModelAttribute("bookingRequest") BookingRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra,
                         @RequestParam(name = "carId", required = false) Integer carId) {
        if (principal == null) return "redirect:/login";
        Customer customer = customerService.findByAccountId(principal.getAccount().getId());
        if (customer == null) {
            ra.addFlashAttribute("toastMessage", "Please complete your profile first");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/profile";
        }
        if (binding.hasErrors()) {
            ra.addFlashAttribute("toastMessage", "Please fix the errors in the booking form.");
            ra.addFlashAttribute("toastType", "error");
            if (carId != null) {
                return "redirect:/customer/bookings/cars/" + carId;
            }
            List<Car> cars = carService.search(null, "Available", 1, 100).getContent();
            Map<Integer, CarProducer> pm = new HashMap<>();
            for (Car c : cars) {
                if (c.getProducerId() != null) {
                    pm.computeIfAbsent(c.getProducerId(), id -> producerService.findById(id));
                }
            }
            model.addAttribute("cars", cars);
            model.addAttribute("producerMap", pm);
            return "customer/bookings/new";
        }
        try {
            CarRentalRequest rentalReq = CarRentalRequest.builder()
                    .carIds(request.getCarIds())
                    .pickupDate(request.getPickupDate())
                    .returnDate(request.getReturnDate())
                    .build();
            rentalService.createRentals(customer.getId(), rentalReq);
            ra.addFlashAttribute("toastMessage", "Request sent. Admin will review and approve.");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
            if (carId != null) {
                return "redirect:/customer/bookings/cars/" + carId;
            }
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
        if (!"Pending".equals(rental.getStatus())) {
            ra.addFlashAttribute("toastMessage", "Only pending requests can be cancelled");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/customer/bookings";
        }
        Integer carId = rental.getCarId();
        rentalService.delete(id);
        Car car = carService.findById(carId);
        if (car != null && "Unavailable".equals(car.getStatus())) {
            car.setStatus("Available");
        }
        ra.addFlashAttribute("toastMessage", "Request cancelled. Car is still available.");
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