package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarRentalRequest {

    @NotEmpty(message = "Select at least one car")
    private List<Integer> carIds;

    @NotNull(message = "Pickup date is required")
    private LocalDate pickupDate;

    @NotNull(message = "Return date is required")
    private LocalDate returnDate;

    @NotNull(message = "Rent price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal rentPrice;

    @Size(max = 10)
    private String status;
}