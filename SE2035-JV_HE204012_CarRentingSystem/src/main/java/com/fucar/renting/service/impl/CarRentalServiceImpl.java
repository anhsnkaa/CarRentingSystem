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
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "pickupDate"));
        return carRentalRepository.search(status, from, to, pageable);
    }

    @Override
    public Page<CarRental> findByCustomer(Integer customerId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "pickupDate"));
        return carRentalRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public CarRental findById(Integer id) {
        return carRentalRepository.findById(id).orElse(null);
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
                ? "Active" : request.getStatus();

        for (Integer carId : request.getCarIds()) {
            Car car = carRepository.findById(carId).orElseThrow(
                    () -> new RuntimeException("Car not found: " + carId));
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
            car.setStatus("Rented");
            carRepository.save(car);
        }
        return saved;
    }

    @Override
    @Transactional
    public CarRental update(Integer id, CarRentalRequest request) {
        CarRental r = carRentalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rental not found: " + id));
        if (!request.getReturnDate().isAfter(request.getPickupDate())) {
            throw new IllegalArgumentException("Return date must be after pickup date");
        }
        r.setPickupDate(request.getPickupDate());
        r.setReturnDate(request.getReturnDate());
        r.setRentPrice(request.getRentPrice());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            r.setStatus(request.getStatus());
        }
        return carRentalRepository.save(r);
    }

    @Override
    @Transactional
    public CarRental complete(Integer id) {
        CarRental r = carRentalRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Rental not found: " + id));
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