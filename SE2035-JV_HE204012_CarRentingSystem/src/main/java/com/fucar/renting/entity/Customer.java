package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "identity_card", nullable = false)
    private String identityCard;

    @Column(name = "licence_number", nullable = false)
    private String licenceNumber;

    @Column(name = "licence_date", nullable = false)
    private LocalDate licenceDate;

    @Column(name = "account_id", nullable = false)
    private Long accountId;
}
