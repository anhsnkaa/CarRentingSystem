package com.fucar.renting.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @NotNull
    private Integer carRenId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer reviewStar;

    @NotBlank
    @Size(max = 500)
    private String comment;
}