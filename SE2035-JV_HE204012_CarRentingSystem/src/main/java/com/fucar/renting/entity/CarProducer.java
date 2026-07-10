package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_producers")
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarProducer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producer_id")
    private Long id;

    @Column(name = "producer_name", nullable = false)
    private String producerName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "country", nullable = false)
    private String country;
}
