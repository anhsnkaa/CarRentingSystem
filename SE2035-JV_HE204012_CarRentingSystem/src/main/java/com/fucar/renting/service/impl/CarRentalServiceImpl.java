package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CarRentalRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.repository.CarRepository;
import com.fucar.renting.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarRentalServiceImpl implements CarRentalService {

    private final CarRentalRepository carRentalRepository;
    private final CarRepository carRepository;

    @Override
    public Page<CarRental> search(String status, LocalDate from, LocalDate to,
                                  Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return carRentalRepository.search(status, from, to, pageable);
    }

    @Override
    public Page<CarRental> findByCustomer(Integer customerId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return carRentalRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public CarRental findById(Integer id) {
        return carRentalRepository.findById(id).orElse(null);
    }

    @Override
    public List<CarRental> findAllByStatus(String status) {
        return carRentalRepository.findAllByStatusOrderByIdDesc(status);
    }

    @Override
    @Transactional
    public List<CarRental> createRentals(Integer customerId, CarRentalRequest request) {
        if (!request.getReturnDate().isAfter(request.getPickupDate())) {
            throw new IllegalArgumentException("Return date must be after pickup date");
        }
        long days = ChronoUnit.DAYS.between(request.getPickupDate(), request.getReturnDate());
        if (days <= 0) days = 1;

        List<CarRental> saved = new ArrayList<>();
        String status = (request.getStatus() == null || request.getStatus().isBlank())
                ? "Pending" : request.getStatus();

        java.util.List<String> activeStatuses = java.util.Arrays.asList("Pending", "Active");

        for (Integer carId : request.getCarIds()) {
            if (carRentalRepository.countByCustomerIdAndCarIdAndStatusIn(customerId, carId, activeStatuses) > 0) {
                throw new IllegalArgumentException("You already have a pending or active request for this car (ID: " + carId + "). Please wait for admin to process it first.");
            }
            Car car = carRepository.findById(carId).orElseThrow(
                    () -> new RuntimeException("Car not found: " + carId));
            if (!"Available".equals(car.getStatus())) {
                throw new IllegalArgumentException("Car is no longer available: " + car.getCarName());
            }
            BigDecimal totalPrice = car.getRentPrice().multiply(BigDecimal.valueOf(days));
            CarRental r = CarRental.builder()
                    .customerId(customerId)
                    .carId(carId)
                    .pickupDate(request.getPickupDate())
                    .returnDate(request.getReturnDate())
                    .rentPrice(totalPrice)
                    .status(status)
                    .build();
            saved.add(carRentalRepository.save(r));
            car.setStatus("Unavailable");
            carRepository.save(car);
        }
        return saved;
    }

    @Override
    @Transactional
    public CarRental approve(Integer id) {
        CarRental r = carRentalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rental not found: " + id));
        if (!"Pending".equals(r.getStatus())) {
            throw new IllegalArgumentException("Only Pending rentals can be approved");
        }
        Car car = carRepository.findById(r.getCarId()).orElseThrow(
                () -> new RuntimeException("Car not found: " + r.getCarId()));
        if (!"Available".equals(car.getStatus())) {
            throw new IllegalArgumentException("Car '" + car.getCarName() + "' is no longer available (someone else booked it first). Cannot approve this request.");
        }
        r.setStatus("Active");
        CarRental saved = carRentalRepository.save(r);
        car.setStatus("Rented");
        carRepository.save(car);
        return saved;
    }

    @Override
    @Transactional
    public CarRental reject(Integer id) {
        CarRental r = carRentalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rental not found: " + id));
        if ("Active".equals(r.getStatus()) || "Completed".equals(r.getStatus())) {
            throw new IllegalArgumentException("Cannot reject an active or completed rental");
        }
        r.setStatus("Cancelled");
        CarRental saved = carRentalRepository.save(r);
        Car car = carRepository.findById(r.getCarId()).orElse(null);
        if (car != null && "Unavailable".equals(car.getStatus())) {
            car.setStatus("Available");
            carRepository.save(car);
        }
        return saved;
    }

    @Override
    @Transactional
    public CarRental complete(Integer id) {
        CarRental r = carRentalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rental not found: " + id));
        if (!"Active".equals(r.getStatus())) {
            throw new IllegalArgumentException("Only Active rentals can be marked as returned");
        }
        r.setStatus("Completed");
        CarRental saved = carRentalRepository.save(r);
        Car car = carRepository.findById(r.getCarId()).orElse(null);
        if (car != null) {
            car.setStatus("Available");
            carRepository.save(car);
        }
        return saved;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        carRentalRepository.deleteById(id);
    }
}