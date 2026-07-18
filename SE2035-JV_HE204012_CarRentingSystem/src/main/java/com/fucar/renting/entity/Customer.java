package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Customer")
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
    @Column(name = "CustomerID")
    private Integer id;

    @Column(name = "FullName", nullable = false, length = 200)
    private String fullName;

    @Column(name = "Mobile", nullable = false, length = 15)
    private String mobile;

    @Column(name = "Birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "IdentityCard", nullable = false, length = 20)
    private String identityCard;

    @Column(name = "LicenceNumber", nullable = false, length = 20)
    private String licenceNumber;

    @Column(name = "LicenceDate", nullable = false)
    private LocalDate licenceDate;

    @Column(name = "AccountID", nullable = false)
    private Integer accountId;
}