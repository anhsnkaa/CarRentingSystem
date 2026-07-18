package com.fucar.renting.controller;

import com.fucar.renting.repository.CarProducerRepository;
import com.fucar.renting.repository.CarRepository;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.repository.CustomerRepository;
import com.fucar.renting.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CarProducerRepository carProducerRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final CarRentalRepository carRentalRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("producerCount", carProducerRepository.count());
        model.addAttribute("carCount", carRepository.count());
        model.addAttribute("customerCount", customerRepository.count());
        model.addAttribute("rentalCount", carRentalRepository.count());
        return "admin/dashboard";
    }
}