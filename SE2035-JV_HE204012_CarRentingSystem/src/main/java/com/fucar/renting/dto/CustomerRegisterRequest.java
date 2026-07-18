package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    private String fullName;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @NotNull(message = "Birthday is required")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @NotBlank(message = "Identity card is required")
    private String identityCard;

    @NotBlank(message = "Licence number is required")
    private String licenceNumber;

    @NotNull(message = "Licence date is required")
    @Past(message = "Licence date must be in the past")
    private LocalDate licenceDate;
}
