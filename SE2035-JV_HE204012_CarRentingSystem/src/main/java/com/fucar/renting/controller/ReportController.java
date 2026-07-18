package com.fucar.renting.controller;

import com.fucar.renting.dto.ReportRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.service.CarRentalService;
import com.fucar.renting.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final CarRentalService rentalService;
    private final CarService carService;
    private final CarRentalRepository carRentalRepository;

    @GetMapping
    public String report(@Valid @ModelAttribute("reportRequest") ReportRequest request,
                         BindingResult binding,
                         Model model) {
        if (binding.hasErrors() || request.getStartDate() == null || request.getEndDate() == null) {
            model.addAttribute("reportRequest",
                    ReportRequest.builder()
                            .startDate(LocalDate.now().minusMonths(1))
                            .endDate(LocalDate.now())
                            .build());
            model.addAttribute("activeMenu", "reports");
            return "admin/reports/by-date";
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            model.addAttribute("error", "End date must be after start date");
            model.addAttribute("reportRequest", request);
            model.addAttribute("activeMenu", "reports");
            return "admin/reports/by-date";
        }

        List<CarRental> rentals = carRentalRepository.findInPeriod(
                request.getStartDate(), request.getEndDate());

        Map<Integer, Car> carMap = new HashMap<>();
        for (CarRental r : rentals) {
            carMap.computeIfAbsent(r.getCarId(), id -> carService.findById(id));
        }

        BigDecimal totalRevenue = rentals.stream()
                .map(CarRental::getRentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Integer, Long> countByCar = rentals.stream()
                .collect(java.util.stream.Collectors.groupingBy(CarRental::getCarId,
                        java.util.stream.Collectors.counting()));

        model.addAttribute("rentals", rentals);
        model.addAttribute("carMap", carMap);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("rentalCount", rentals.size());
        model.addAttribute("countByCar", countByCar);
        model.addAttribute("reportRequest", request);
        model.addAttribute("activeMenu", "reports");
        return "admin/reports/by-date";
    }
}