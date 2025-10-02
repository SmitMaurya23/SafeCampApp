package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;

public interface AuthService {
    String signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
