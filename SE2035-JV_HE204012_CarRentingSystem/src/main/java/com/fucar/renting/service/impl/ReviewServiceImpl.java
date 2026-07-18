package com.fucar.renting.service.impl;

import com.fucar.renting.dto.ReviewRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.repository.CustomerRepository;
import com.fucar.renting.repository.ReviewRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.fucar.renting.service.CarService;
import com.fucar.renting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CarRentalRepository carRentalRepository;
    private final CustomerRepository customerRepository;
    private final CarService carService;

    @Override
    public List<Review> findByCarRental(Integer carRenId) {
        return reviewRepository.findByCarRenIdOrderByIdDesc(carRenId);
    }

    @Override
    public List<Review> findByCarId(Integer carId) {
        List<CarRental> rentals = carRentalRepository.findByCarIdOrderByPickupDateDesc(carId);
        List<Review> reviews = new ArrayList<>();
        for (CarRental r : rentals) {
            reviews.addAll(reviewRepository.findByCarRenIdOrderByIdDesc(r.getId()));
        }
        reviews.sort(Comparator.comparing(Review::getId).reversed());
        return reviews;
    }

    @Override
    public Map<Integer, Customer> buildCustomerMap(List<Review> reviews) {
        Map<Integer, Customer> map = new HashMap<>();
        for (Review rv : reviews) {
            if (map.containsKey(rv.getCarRenId())) continue;
            CarRental rental = carRentalRepository.findById(rv.getCarRenId()).orElse(null);
            if (rental != null) {
                Customer cust = customerRepository.findById(rental.getCustomerId()).orElse(null);
                if (cust != null) map.put(rv.getCarRenId(), cust);
            }
        }
        return map;
    }

    @Override
    public Map<Integer, Car> buildCarMap(List<Review> reviews) {
        Map<Integer, Car> map = new HashMap<>();
        for (Review rv : reviews) {
            if (map.containsKey(rv.getCarRenId())) continue;
            CarRental rental = carRentalRepository.findById(rv.getCarRenId()).orElse(null);
            if (rental != null) {
                Car car = carService.findById(rental.getCarId());
                if (car != null) map.put(rv.getCarRenId(), car);
            }
        }
        return map;
    }

    @Override
    public List<Review> findLatest(int limit) {
        return reviewRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }

    @Override
    @Transactional
    public Review create(Integer customerId, ReviewRequest request) {
        CarRental rental = carRentalRepository.findById(request.getCarRenId())
                .orElseThrow(() -> new RuntimeException("Rental not found: " + request.getCarRenId()));
        Customer customer = customerRepository.findByAccountId(customerId);
        if (customer == null || !rental.getCustomerId().equals(customer.getId())) {
            throw new RuntimeException("You can only review your own rentals");
        }
        Review review = Review.builder()
                .carRenId(request.getCarRenId())
                .reviewStar(request.getReviewStar())
                .comment(request.getComment())
                .build();
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        reviewRepository.deleteById(id);
    }
}