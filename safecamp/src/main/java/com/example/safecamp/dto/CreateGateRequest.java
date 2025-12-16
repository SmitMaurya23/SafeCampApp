package com.example.safecamp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGateRequest {
    @NotBlank(message="name must not be blank!!")
    private String name;
    @NotBlank(message="location must not be blank!!")
    private String location;
}


