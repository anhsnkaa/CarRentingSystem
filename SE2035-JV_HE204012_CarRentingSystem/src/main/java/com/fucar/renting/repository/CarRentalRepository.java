package com.fucar.renting.repository;

import com.fucar.renting.entity.CarRental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {

    Page<CarRental> findByCustomerId(Integer customerId, Pageable pageable);

    long countByCustomerId(Integer customerId);

    @Query("SELECT r FROM CarRental r WHERE " +
            "(:status IS NULL OR :status = '' OR r.status = :status) " +
            "AND (:from IS NULL OR r.pickupDate >= :from) " +
            "AND (:to IS NULL OR r.pickupDate <= :to)")
    Page<CarRental> search(@Param("status") String status,
                           @Param("from") LocalDate from,
                           @Param("to") LocalDate to,
                           Pageable pageable);

    @Query("SELECT r FROM CarRental r " +
            "WHERE r.pickupDate >= :from AND r.pickupDate <= :to " +
            "ORDER BY r.pickupDate DESC")
    List<CarRental> findInPeriod(@Param("from") LocalDate from,
                                 @Param("to") LocalDate to);

    @Query("SELECT COUNT(r) FROM CarRental r WHERE r.carId = :carId")
    long countByCarId(@Param("carId") Integer carId);

    long countByCarIdAndStatus(Integer carId, String status);

    long countByCustomerIdAndCarIdAndStatusIn(Integer customerId, Integer carId, java.util.Collection<String> statuses);

    List<CarRental> findByCustomerIdAndStatusIn(Integer customerId, java.util.Collection<String> statuses);

    List<CarRental> findByCarIdOrderByPickupDateDesc(Integer carId);

    List<CarRental> findAllByStatusOrderByIdDesc(String status);
}