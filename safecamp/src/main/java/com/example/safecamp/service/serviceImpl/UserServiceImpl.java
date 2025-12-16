package com.example.safecamp.service.serviceImpl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.safecamp.dto.CreateUserRequest;
import com.example.safecamp.dto.UserResponse;
import com.example.safecamp.entity.User;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setRole(savedUser.getRole());

        return response;
    }

    public UserResponse getUserById(UUID id) {
        if (id == null || !userRepository.existsById(id)) {
            throw new IllegalArgumentException("No such user exist!!");
        }
        User fetchedUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such user exist!!"));
        
        UserResponse response = new UserResponse();
        response.setId(fetchedUser.getId());
        response.setName(fetchedUser.getName());
        response.setEmail(fetchedUser.getEmail());
        response.setPhone(fetchedUser.getPhone());
        response.setRole(fetchedUser.getRole());
        
        return response;
    }

}
