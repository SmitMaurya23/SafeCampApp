package com.example.safecamp.service;

import com.example.safecamp.security.RegisterRequest;
import com.example.safecamp.security.RegisterResponse;

public interface RegistrationService {
    RegisterResponse register(RegisterRequest request);
}
