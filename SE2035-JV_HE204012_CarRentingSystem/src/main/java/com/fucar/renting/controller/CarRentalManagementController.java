package com.fucar.renting.controller;

import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.service.CarProducerService;
import com.fucar.renting.service.CarRentalService;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/rentals")
@RequiredArgsConstructor
public class CarRentalManagementController {

    private final CarRentalService rentalService;
    private final CarService carService;
    private final CarProducerService producerService;
    private final CustomerService customerService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String status,
                       @RequestParam(required = false) LocalDate from,
                       @RequestParam(required = false) LocalDate to,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<CarRental> rentals = rentalService.search(status, from, to, page, size);

        List<CarRental> activeRentals = rentalService.findAllByStatus("Active");

        Map<Integer, Customer> customerMap = new HashMap<>();
        Map<Integer, Car> carMap = new HashMap<>();
        Map<Integer, CarProducer> producerMap = new HashMap<>();

        List<CarRental> combined = new ArrayList<>(rentals.getContent());
        combined.addAll(activeRentals);
        for (CarRental r : combined) {
            if (!customerMap.containsKey(r.getCustomerId())) {
                Customer c = customerService.findById(r.getCustomerId());
                if (c != null) customerMap.put(r.getCustomerId(), c);
            }
            if (!carMap.containsKey(r.getCarId())) {
                Car car = carService.findById(r.getCarId());
                if (car != null) {
                    carMap.put(r.getCarId(), car);
                    if (car.getProducerId() != null) {
                        producerMap.computeIfAbsent(car.getProducerId(),
                                id -> producerService.findById(id));
                    }
                }
            }
        }

        model.addAttribute("rentals", rentals);
        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("activeCount", activeRentals.size());
        model.addAttribute("customerMap", customerMap);
        model.addAttribute("carMap", carMap);
        model.addAttribute("producerMap", producerMap);
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
        Customer customer = customerService.findById(rental.getCustomerId());
        CarProducer producer = car != null && car.getProducerId() != null
                ? producerService.findById(car.getProducerId()) : null;
        model.addAttribute("rental", rental);
        model.addAttribute("car", car);
        model.addAttribute("producer", producer);
        model.addAttribute("customer", customer);
        model.addAttribute("activeMenu", "rentals");
        return "admin/rentals/detail";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            rentalService.complete(id);
            ra.addFlashAttribute("toastMessage", "Car returned. Status set to Completed.");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/rentals";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            rentalService.approve(id);
            ra.addFlashAttribute("toastMessage", "Rental approved. Car is now Rented.");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/rentals";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            rentalService.reject(id);
            ra.addFlashAttribute("toastMessage", "Rental rejected.");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/rentals";
    }
}