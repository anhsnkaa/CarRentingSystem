package com.fucar.renting.repository;

import com.fucar.renting.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByCarRenIdOrderByIdDesc(Integer carRenId);
}