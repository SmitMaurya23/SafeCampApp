package com.example.auth.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Signup request payload")
public class SignupRequest {
    @Schema(example = "Smit Sharma")
    private String name;

    @Schema(example = "smit@example.com")
    private String email;

    @Schema(example = "securePassword1234")
    private String password;

    @Schema(example = "RESIDENT")
    private String role;
}
