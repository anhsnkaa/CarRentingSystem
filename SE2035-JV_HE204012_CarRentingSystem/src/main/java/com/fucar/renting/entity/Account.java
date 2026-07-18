package com.fucar.renting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Account")
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Integer id;

    @Column(name = "AccountName", nullable = false, length = 100)
    private String accountName;

    @Column(name = "Email", nullable = false, length = 200)
    private String email;

    @Column(name = "Password", nullable = false, length = 200)
    private String password;

    @Column(name = "Role", nullable = false, length = 10)
    private String role;
}