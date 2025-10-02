package com.example.auth.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;
import com.example.auth.security.JwtUtil;
import com.example.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${firebase.gateway.url}")
    private String firebaseGatewayUrl;

    @Override
    public String signup(SignupRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", request.getName());
        payload.put("email", request.getEmail());
        payload.put("password", request.getPassword());
        payload.put("role", request.getRole());
        payload.put("approved", false);

        restTemplate.postForEntity(firebaseGatewayUrl + "/save", payload, String.class);

        return "SignUp request submitted. Awaiting admin approval.";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                firebaseGatewayUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {
                });

        Map<String, Object> userData = response.getBody();

        if (userData == null || !userData.containsKey("role")) {
            throw new RuntimeException("Invalid login response from Firebase Gateway");
        }

        String role = (String) userData.get("role");
        Boolean isApproved = (Boolean) userData.get("approved");

        String token = jwtUtil.generateToken(request.getEmail(), role, isApproved);

        return new AuthResponse(token, role, isApproved);
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        logger.info("Validating token...");

        if (!jwtUtil.validateToken(token)) {
            logger.warn("Token is invalid or expired");
            throw new RuntimeException("Token is invalid or expired");
        }

        Map<String, Object> claims = jwtUtil.extractAllClaims(token);
        logger.info("Token claims extracted: {}", claims);

        return claims;
    }

}
