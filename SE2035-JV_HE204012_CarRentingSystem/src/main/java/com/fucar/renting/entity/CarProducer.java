package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CarProducer")
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
    @Column(name = "ProducerID")
    private Integer id;

    @Column(name = "ProducerName", nullable = false, length = 100)
    private String producerName;

    @Column(name = "Address", nullable = false, length = 200)
    private String address;

    @Column(name = "Country", nullable = false, length = 100)
    private String country;
}