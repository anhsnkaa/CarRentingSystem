package com.fucar.renting.service.impl;

import com.fucar.renting.dto.ReviewRequest;
import com.fucar.renting.entity.CarRental;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.entity.Review;
import com.fucar.renting.repository.CarRentalRepository;
import com.fucar.renting.repository.CustomerRepository;
import com.fucar.renting.repository.ReviewRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.fucar.renting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CarRentalRepository carRentalRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<Review> findByCarRental(Integer carRenId) {
        return reviewRepository.findByCarRenIdOrderByIdDesc(carRenId);
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