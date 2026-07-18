package com.fucar.renting.controller;

import com.fucar.renting.dto.ReportRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.service.CarProducerService;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final CarRentalService rentalService;
    private final CarService carService;
    private final CarProducerService producerService;
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
            Integer carId = r.getCarId();
            if (!carMap.containsKey(carId)) {
                Car c = carService.findById(carId);
                if (c != null) carMap.put(carId, c);
            }
        }

        Map<Integer, CarProducer> producerMap = new HashMap<>();
        for (Car c : carMap.values()) {
            Integer pid = c.getProducerId();
            if (pid != null && !producerMap.containsKey(pid)) {
                CarProducer p = producerService.findById(pid);
                if (p != null) producerMap.put(pid, p);
            }
        }

        BigDecimal totalRevenue = rentals.stream()
                .map(CarRental::getRentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeCount = rentals.stream().filter(r -> "Active".equals(r.getStatus())).count();
        long completedCount = rentals.stream().filter(r -> "Completed".equals(r.getStatus())).count();
        long cancelledCount = rentals.stream().filter(r -> "Cancelled".equals(r.getStatus())).count();

        BigDecimal avgRentalValue = rentals.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(rentals.size()), 0, RoundingMode.HALF_UP);

        Map<Integer, Long> countByCar = rentals.stream()
                .collect(Collectors.groupingBy(CarRental::getCarId, Collectors.counting()));

        Map<Integer, Long> daysByCar = rentals.stream()
                .collect(Collectors.groupingBy(
                        CarRental::getCarId,
                        Collectors.summingLong(r ->
                                ChronoUnit.DAYS.between(r.getPickupDate(), r.getReturnDate()))));

        Map<Integer, BigDecimal> revenueByCar = rentals.stream()
                .collect(Collectors.groupingBy(
                        CarRental::getCarId,
                        Collectors.reducing(BigDecimal.ZERO, CarRental::getRentPrice, BigDecimal::add)));

        List<Map.Entry<Integer, BigDecimal>> topCars = revenueByCar.entrySet().stream()
                .sorted(Map.Entry.<Integer, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("rentals", rentals);
        model.addAttribute("carMap", carMap);
        model.addAttribute("producerMap", producerMap);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("rentalCount", rentals.size());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("avgRentalValue", avgRentalValue);
        model.addAttribute("countByCar", countByCar);
        model.addAttribute("daysByCar", daysByCar);
        model.addAttribute("revenueByCar", revenueByCar);
        model.addAttribute("topCars", topCars);
        model.addAttribute("reportRequest", request);
        model.addAttribute("activeMenu", "reports");
        return "admin/reports/by-date";
    }
}