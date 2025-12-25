package com.example.safecamp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateEmergencyAlertRequest {
    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @Size(max=255)
    private String message;
}
