package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String fullName;

    @NotBlank
    @Size(max = 15)
    private String mobile;

    @NotNull
    @Past
    private LocalDate birthday;

    @NotBlank
    @Size(max = 20)
    private String identityCard;

    @NotBlank
    @Size(max = 20)
    private String licenceNumber;

    @NotNull
    @Past
    private LocalDate licenceDate;
}