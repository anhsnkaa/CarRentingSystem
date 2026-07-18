package com.fucar.renting.controller;

import com.fucar.renting.config.AuthUtil;
import com.fucar.renting.entity.Account;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.Review;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CarService carService;
    private final ReviewService reviewService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        Account acc = AuthUtil.currentAccount();
        if (acc != null) {
            if (AuthUtil.isAdmin()) return "redirect:/admin/dashboard";
            return "redirect:/customer/dashboard";
        }
        List<Car> featuredCars = carService.search(null, "", 1, 6).getContent();
        List<Review> latestReviews = reviewService.findLatest(5);
        Map<Integer, Car> reviewCarMap = reviewService.buildCarMap(latestReviews);
        Map<Integer, com.fucar.renting.entity.Customer> reviewCustomerMap = reviewService.buildCustomerMap(latestReviews);
        long availableCount = featuredCars.stream()
                .filter(c -> "Available".equals(c.getStatus()))
                .count();
        model.addAttribute("featuredCars", featuredCars);
        model.addAttribute("latestReviews", latestReviews);
        model.addAttribute("reviewCarMap", reviewCarMap);
        model.addAttribute("reviewCustomerMap", reviewCustomerMap);
        model.addAttribute("carCount", availableCount);
        return "home";
    }
}