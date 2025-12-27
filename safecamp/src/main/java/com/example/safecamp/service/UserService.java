package com.example.safecamp.service;

import java.util.List;
import java.util.UUID;


import com.example.safecamp.dto.ChangePasswordRequest;
import com.example.safecamp.dto.CreateUserRequest;
import com.example.safecamp.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID id);

    List<UserResponse> getUserByName(String name);

    List<UserResponse> getUserByRole(String role);

    void changePassword(ChangePasswordRequest request, UUID userId);

}
