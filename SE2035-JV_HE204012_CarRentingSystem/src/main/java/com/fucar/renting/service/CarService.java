package com.fucar.renting.service;

import com.fucar.renting.dto.CarRequest;
import com.fucar.renting.entity.Car;
import org.springframework.data.domain.Page;

public interface CarService {

    Page<Car> search(String keyword, String status, Integer page, Integer size);

    Car findById(Integer id);

    Car create(CarRequest request);

    Car update(Integer id, CarRequest request);

    void delete(Integer id);

    void softDelete(Integer id);

    java.util.List<Car> findTopAvailable(int limit);
}