package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Review")
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
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CarRenID", nullable = false)
    private Integer carRenId;

    @Column(name = "ReviewStar", nullable = false)
    private Integer reviewStar;

    @Column(name = "Comment", nullable = false, length = 500)
    private String comment;
}