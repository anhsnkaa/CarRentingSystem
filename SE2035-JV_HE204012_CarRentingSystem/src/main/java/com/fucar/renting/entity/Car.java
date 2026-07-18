package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Car")
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarID")
    private Integer id;

    @Column(name = "CarName", nullable = false, length = 200)
    private String carName;

    @Column(name = "CarModelYear", nullable = false)
    private Integer carModelYear;

    @Column(name = "Color", nullable = false, length = 50)
    private String color;

    @Column(name = "Capacity", nullable = false)
    private Integer capacity;

    @Column(name = "Description", nullable = false, length = 1000)
    private String description;

    @Column(name = "ImportDate", nullable = false)
    private LocalDate importDate;

    @Column(name = "ProducerID", nullable = false)
    private Integer producerId;

    @Column(name = "RentPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentPrice;

    @Column(name = "Status", nullable = false, length = 10)
    private String status;
}