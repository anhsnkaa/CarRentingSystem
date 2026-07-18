package com.fucar.renting.service;

import com.fucar.renting.dto.ReviewRequest;
import com.fucar.renting.entity.Review;

import java.util.List;

public interface ReviewService {

    List<Review> findByCarRental(Integer carRenId);

    List<Review> findLatest(int limit);

    Review create(Integer customerId, ReviewRequest request);

    void delete(Integer id);
}