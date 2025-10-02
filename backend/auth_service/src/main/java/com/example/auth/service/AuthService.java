package com.example.auth.service;

import java.util.Map;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;

public interface AuthService {
    String signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    Map<String, Object> validateToken(String token);

}
