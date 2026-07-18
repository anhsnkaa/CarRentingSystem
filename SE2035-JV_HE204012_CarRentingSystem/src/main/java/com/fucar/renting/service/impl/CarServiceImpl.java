package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CarRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.repository.CarRepository;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;

    @Override
    public Page<Car> search(String keyword, String status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return carRepository.search(keyword, status, pageable);
    }

    @Override
    public Car findById(Integer id) {
        return carRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Car create(CarRequest request) {
        Car c = Car.builder()
                .carName(request.getCarName())
                .carModelYear(request.getCarModelYear())
                .color(request.getColor())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .importDate(request.getImportDate())
                .producerId(request.getProducerId())
                .rentPrice(request.getRentPrice())
                .status(request.getStatus())
                .build();
        return carRepository.save(c);
    }

    @Override
    @Transactional
    public Car update(Integer id, CarRequest request) {
        Car c = carRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Car not found: " + id));
        c.setCarName(request.getCarName());
        c.setCarModelYear(request.getCarModelYear());
        c.setColor(request.getColor());
        c.setCapacity(request.getCapacity());
        c.setDescription(request.getDescription());
        c.setImportDate(request.getImportDate());
        c.setProducerId(request.getProducerId());
        c.setRentPrice(request.getRentPrice());
        c.setStatus(request.getStatus());
        return carRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        long activeCount = carRentalRepository.countByCarIdAndStatus(id, "Active");
        long totalCount = carRentalRepository.countByCarId(id);
        if (activeCount > 0) {
            throw new IllegalArgumentException("Cannot delete: this car is currently rented (Active). Mark rental as returned first.");
        }
        if (totalCount > 0) {
            throw new IllegalArgumentException("Cannot delete: this car has rental history. Remove or archive related records first.");
        }
        carRepository.deleteById(id);
    }

    @Override
    public boolean hasActiveRental(Integer carId) {
        return carRentalRepository.countByCarIdAndStatus(carId, "Active") > 0;
    }

    @Override
    public List<Car> findTopAvailable(int limit) {
        return carRepository.search(null, "Available",
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }
}