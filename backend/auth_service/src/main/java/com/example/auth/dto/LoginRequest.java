package com.example.auth.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Login request payload")
public class LoginRequest {
    @Schema(example = "smit@example.com")
    private String email;

    @Schema(example = "securePassword123")
    private String password;

    @Schema(example = "RESIDENT")
    private String role;
}
