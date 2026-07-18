package com.fucar.renting.service;

import com.fucar.renting.dto.CarRentalRequest;
import com.fucar.renting.entity.CarRental;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalService {

    Page<CarRental> search(String status, LocalDate from, LocalDate to, Integer page, Integer size);

    Page<CarRental> findByCustomer(Integer customerId, Integer page, Integer size);

CarRental findById(Integer id);

    List<CarRental> findAllByStatus(String status);

    List<CarRental> createRentals(Integer customerId, CarRentalRequest request);

    CarRental complete(Integer id);

    CarRental approve(Integer id);

    CarRental reject(Integer id);

    void delete(Integer id);
}