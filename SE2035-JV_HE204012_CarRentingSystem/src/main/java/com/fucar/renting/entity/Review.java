package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "car_ren_id", nullable = false)
    private Long carRenId;

    @Column(name = "review_star", nullable = false)
    private Integer reviewStar;

    @Column(name = "comment", nullable = false, length = 1000)
    private String comment;
}
