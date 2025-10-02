package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "Authentication response with JWT")
public class AuthResponse {
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;

    @Schema(example = "RESIDENT")
    private String role;

    @Schema(example = "true")
    private boolean approved;
}
