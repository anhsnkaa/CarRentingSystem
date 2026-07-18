package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CarRental")
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarRenID")
    private Integer id;

    @Column(name = "CustomerID", nullable = false)
    private Integer customerId;

    @Column(name = "CarID", nullable = false)
    private Integer carId;

    @Column(name = "PickupDate", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "ReturnDate", nullable = false)
    private LocalDate returnDate;

    @Column(name = "RentPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentPrice;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;
}