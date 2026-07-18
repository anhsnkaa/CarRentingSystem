package com.fucar.renting.repository;

import com.fucar.renting.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("SELECT c FROM Car c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(c.carName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR c.status = :status)")
    Page<Car> search(@Param("keyword") String keyword,
                     @Param("status") String status,
                     Pageable pageable);

    @Query("SELECT COUNT(r) FROM CarRental r WHERE r.carId = :carId")
    long countRentalsByCarId(@Param("carId") Integer carId);
}