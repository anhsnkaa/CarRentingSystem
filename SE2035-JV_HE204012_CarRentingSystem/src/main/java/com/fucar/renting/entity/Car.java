package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cars")
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
    @Column(name = "car_id")
    private Long id;

    @Column(name = "car_name", nullable = false)
    private String carName;

    @Column(name = "car_model_year", nullable = false)
    private Integer carModelYear;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "import_date", nullable = false)
    private LocalDate importDate;

    @Column(name = "rent_price", nullable = false)
    private BigDecimal rentPrice;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "producer_id", nullable = false)
    private Long producerId;
}
