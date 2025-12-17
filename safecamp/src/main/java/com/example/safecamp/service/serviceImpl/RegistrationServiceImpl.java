package com.example.safecamp.service.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.security.RegisterRequest;
import com.example.safecamp.security.RegisterResponse;
import com.example.safecamp.service.RegistrationService;

import jakarta.transaction.Transactional;
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
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // 3️⃣ Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                "Registration successful",
                savedUser.getRole()
        );
    }
}
