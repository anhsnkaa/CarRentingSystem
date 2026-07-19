package com.fucar.renting.controller;

import com.fucar.renting.config.AuthUtil;
import com.fucar.renting.entity.Account;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;
import com.fucar.renting.service.CarProducerService;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CarService carService;
    private final ReviewService reviewService;
    private final CarProducerService producerService;

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "6") int size,
                       Model model) {
        Account acc = AuthUtil.currentAccount();
        if (acc != null) {
            if (AuthUtil.isAdmin()) return "redirect:/admin/dashboard";
            return "redirect:/customer/dashboard";
        }
        if (page < 1) page = 1;
        if (size < 1) size = 6;

        Page<Car> carPage = carService.search(null, "", page, size);
        List<Car> featuredCars = carPage.getContent();

        Map<Integer, CarProducer> producerMap = new HashMap<>();
        for (Car c : featuredCars) {
            if (c.getProducerId() != null) {
                producerMap.computeIfAbsent(c.getProducerId(),
                        id -> producerService.findById(id));
            }
        }

        List<Review> latestReviews = reviewService.findLatest(5);
        Map<Integer, Car> reviewCarMap = reviewService.buildCarMap(latestReviews);
        Map<Integer, Customer> reviewCustomerMap = reviewService.buildCustomerMap(latestReviews);

        long totalAvailable = carService.search(null, "Available", 1, 1).getTotalElements();

        model.addAttribute("featuredCars", featuredCars);
        model.addAttribute("producerMap", producerMap);
        model.addAttribute("latestReviews", latestReviews);
        model.addAttribute("reviewCarMap", reviewCarMap);
        model.addAttribute("reviewCustomerMap", reviewCustomerMap);
        model.addAttribute("carCount", totalAvailable);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(carPage.getTotalPages(), 1));
        model.addAttribute("totalCars", carPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "home";
    }

    @GetMapping("/cars/{carId}")
    public String carDetail(@PathVariable Integer carId,
                            Model model,
                            RedirectAttributes ra) {
        Car car = carService.findById(carId);
        if (car == null) {
            ra.addFlashAttribute("toastMessage", "Car not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/";
        }
        CarProducer producer = car.getProducerId() != null
                ? producerService.findById(car.getProducerId())
                : null;
        List<Review> reviews = reviewService.findByCarId(carId);
        Map<Integer, Customer> reviewCustomerMap = reviewService.buildCustomerMap(reviews);
        double avgRating = reviews.isEmpty() ? 0.0
                : reviews.stream().mapToInt(Review::getReviewStar).average().orElse(0.0);
        model.addAttribute("car", car);
        model.addAttribute("producer", producer);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCustomerMap", reviewCustomerMap);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("reviewCount", reviews.size());
        return "home/car-detail";
    }
}