package com.example.safecamp.service;

import java.util.UUID;

import com.example.safecamp.dto.CreateGateRequest;
import com.example.safecamp.dto.GateResponse;

public interface GateService {
    GateResponse createGate(CreateGateRequest request);

    GateResponse getGateById(UUID id);
}

// UserResponse createUser(CreateUserRequest request);

// UserResponse getUserById(UUID id);