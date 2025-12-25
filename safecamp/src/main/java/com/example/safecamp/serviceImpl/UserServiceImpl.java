package com.example.safecamp.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.safecamp.dto.ChangePasswordRequest;
import com.example.safecamp.dto.CreateUserRequest;
import com.example.safecamp.dto.UserResponse;
import com.example.safecamp.entity.User;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole())
                .build();
    }

    public UserResponse getUserById(UUID id) {
        if (id == null || !userRepository.existsById(id)) {
            throw new IllegalArgumentException("No such user exist!!");
        }
        User fetchedUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such user exist!!"));

        return UserResponse.builder()
                .id(fetchedUser.getId())
                .name(fetchedUser.getName())
                .email(fetchedUser.getEmail())
                .phone(fetchedUser.getPhone())
                .role(fetchedUser.getRole())
                .build();
    }

    public List<UserResponse> getUserByName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Empty! Enter a valid name");
        }

        List<User> userList = userRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("No such user exist !!!"));

        List<UserResponse> result = new ArrayList<>();
        for (User user : userList) {
            result.add(UserResponse.builder().email(user.getEmail()).id(user.getId()).name(user.getName())
                    .phone(user.getPhone()).role(user.getRole()).build());
        }

        return result;

    }

    public void changePassword(ChangePasswordRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No such user exist !!!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password entered is wrong !!");
        }
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("Old and New password cannot be same !!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    }

}
