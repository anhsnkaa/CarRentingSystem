package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarRequest {

    @NotBlank(message = "Car name is required")
    @Size(max = 200)
    private String carName;

    @NotNull(message = "Model year is required")
    @Min(1900)
    @Max(2100)
    private Integer carModelYear;

    @NotBlank(message = "Color is required")
    @Size(max = 50)
    private String color;

    @NotNull(message = "Capacity is required")
    @Min(1)
    private Integer capacity;

    @NotBlank(message = "Description is required")
    @Size(max = 1000)
    private String description;

    @NotNull(message = "Import date is required")
    @PastOrPresent
    private LocalDate importDate;

    @NotNull(message = "Producer is required")
    private Integer producerId;

    @NotNull(message = "Rent price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal rentPrice;

    @NotBlank(message = "Status is required")
    @Size(max = 10)
    private String status;
}