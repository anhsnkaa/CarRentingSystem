package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarProducerRequest {

    @NotBlank(message = "Producer name is required")
    @Size(max = 100)
    private String producerName;

    @NotBlank(message = "Address is required")
    @Size(max = 200)
    private String address;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;
}