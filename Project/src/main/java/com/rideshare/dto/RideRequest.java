package com.rideshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    @NotBlank(message = "Pickup location is required")
    @Size(min = 3, max = 200, message = "Pickup location must be between 3 and 200 characters")
    private String pickupLocation;

    @NotBlank(message = "Drop location is required")
    @Size(min = 3, max = 200, message = "Drop location must be between 3 and 200 characters")
    private String dropLocation;
}
