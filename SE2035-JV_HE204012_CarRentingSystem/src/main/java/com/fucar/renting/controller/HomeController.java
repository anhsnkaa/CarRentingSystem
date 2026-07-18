package com.fucar.renting.controller;

import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.Review;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CarService carService;
    private final ReviewService reviewService;

    @GetMapping({"/", "/home"})
    public String home(Authentication auth, Model model) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/customer/dashboard";
        }
        List<Car> featuredCars = carService.findTopAvailable(6);
        List<Review> latestReviews = reviewService.findLatest(5);
        model.addAttribute("featuredCars", featuredCars);
        model.addAttribute("latestReviews", latestReviews);
        model.addAttribute("carCount", featuredCars.size());
        return "home";
    }
}