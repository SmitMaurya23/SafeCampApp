package com.example.safecamp.service;

import java.util.UUID;

import com.example.safecamp.dto.CreateUserRequest;
import com.example.safecamp.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID id);
}
