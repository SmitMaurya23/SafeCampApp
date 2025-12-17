package com.example.safecamp.security;

import java.util.UUID;

import com.example.safecamp.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private UUID userId;
    private String message;
    private Role role;
}
