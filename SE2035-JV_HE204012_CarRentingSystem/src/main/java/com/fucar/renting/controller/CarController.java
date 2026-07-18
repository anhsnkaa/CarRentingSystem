package com.fucar.renting.controller;

import com.fucar.renting.dto.CarRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.service.CarProducerService;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final CarProducerService producerService;
    private final ReviewService reviewService;
    private final CarRentalRepository carRentalRepository;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<Car> cars = carService.search(keyword, status, page, size);
        Map<Integer, Long> activeRentalCounts = new HashMap<>();
        for (Car c : cars.getContent()) {
            activeRentalCounts.put(c.getId(), carRentalRepository.countByCarIdAndStatus(c.getId(), "Active"));
        }
        model.addAttribute("cars", cars);
        model.addAttribute("activeRentalCounts", activeRentalCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cars.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("activeMenu", "cars");
        return "admin/cars/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("carRequest", CarRequest.builder()
                .rentPrice(BigDecimal.ZERO)
                .importDate(LocalDate.now())
                .status("Available")
                .build());
        model.addAttribute("producers", producerService.findAllAsList());
        model.addAttribute("activeMenu", "cars");
        return "admin/cars/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("carRequest") CarRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("producers", producerService.findAllAsList());
            return "admin/cars/form";
        }
        carService.create(request);
        ra.addFlashAttribute("toastMessage", "Car created");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/admin/cars";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Car car = carService.findById(id);
        if (car == null) {
            ra.addFlashAttribute("toastMessage", "Car not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/cars";
        }
        CarProducer producer = car.getProducerId() != null
                ? producerService.findById(car.getProducerId())
                : null;
        List<Review> reviews = reviewService.findByCarId(id);
        Map<Integer, Customer> reviewCustomerMap = reviewService.buildCustomerMap(reviews);
        double avgRating = reviews.isEmpty() ? 0.0
                : reviews.stream().mapToInt(Review::getReviewStar).average().orElse(0.0);
        long rentalCount = carRentalRepository.countByCarId(id);
        model.addAttribute("car", car);
        model.addAttribute("producer", producer);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCustomerMap", reviewCustomerMap);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("rentalCount", rentalCount);
        model.addAttribute("activeMenu", "cars");
        return "admin/cars/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Car c = carService.findById(id);
        if (c == null) {
            ra.addFlashAttribute("toastMessage", "Car not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/cars";
        }
        CarRequest req = CarRequest.builder()
                .carName(c.getCarName())
                .carModelYear(c.getCarModelYear())
                .color(c.getColor())
                .capacity(c.getCapacity())
                .description(c.getDescription())
                .importDate(c.getImportDate())
                .producerId(c.getProducerId())
                .rentPrice(c.getRentPrice())
                .status(c.getStatus())
                .build();
        model.addAttribute("carRequest", req);
        model.addAttribute("carId", id);
        model.addAttribute("producers", producerService.findAllAsList());
        model.addAttribute("activeMenu", "cars");
        return "admin/cars/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("carRequest") CarRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("producers", producerService.findAllAsList());
            model.addAttribute("carId", id);
            model.addAttribute("activeMenu", "cars");
            return "admin/cars/form";
        }
        carService.update(id, request);
        ra.addFlashAttribute("toastMessage", "Car updated");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/admin/cars";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            carService.delete(id);
            ra.addFlashAttribute("toastMessage", "Car deleted");
            ra.addFlashAttribute("toastType", "success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("toastMessage", e.getMessage());
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/cars";
    }
}