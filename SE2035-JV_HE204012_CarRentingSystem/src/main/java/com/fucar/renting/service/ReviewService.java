package com.fucar.renting.service;

import com.fucar.renting.dto.ReviewRequest;
import com.fucar.renting.entity.Car;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    List<Review> findByCarRental(Integer carRenId);

    List<Review> findByCarId(Integer carId);

    Map<Integer, Customer> buildCustomerMap(List<Review> reviews);

    Map<Integer, Car> buildCarMap(List<Review> reviews);

    List<Review> findLatest(int limit);

    Review create(Integer customerId, ReviewRequest request);

    void delete(Integer id);
}