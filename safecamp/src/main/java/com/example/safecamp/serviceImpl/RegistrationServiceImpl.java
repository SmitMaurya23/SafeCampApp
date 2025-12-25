package com.example.safecamp.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.security.RegisterRequest;
import com.example.safecamp.security.RegisterResponse;
import com.example.safecamp.service.RegistrationService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        // 1️⃣ Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        if (!((request.getRole() == Role.RESIDENT) || (request.getRole() == Role.GUEST))) {
            throw new IllegalArgumentException("Only Residents and guests can register");
        }

        // 2️⃣ Create User entity
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .role(savedUser.getRole())
                .message("Registration successful")
                .build();
    }
}
