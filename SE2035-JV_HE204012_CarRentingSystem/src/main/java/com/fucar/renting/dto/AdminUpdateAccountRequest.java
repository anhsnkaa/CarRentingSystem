package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateAccountRequest {

    @NotBlank(message = "Account name is required")
    @Size(min = 3, max = 100, message = "Account name must be 3-100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Account name may only contain letters, digits, dot, underscore, or dash")
    private String accountName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 200)
    private String fullName;

    @NotBlank(message = "Mobile is required")
    @Size(max = 15)
    private String mobile;

    @NotNull(message = "Birthday is required")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @NotBlank(message = "Identity card is required")
    @Size(max = 20)
    private String identityCard;

    @NotBlank(message = "Licence number is required")
    @Size(max = 20)
    private String licenceNumber;

    @NotNull(message = "Licence date is required")
    @Past(message = "Licence date must be in the past")
    private LocalDate licenceDate;
}
