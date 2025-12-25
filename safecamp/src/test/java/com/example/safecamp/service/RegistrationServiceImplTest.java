package com.example.safecamp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.security.RegisterRequest;
import com.example.safecamp.security.RegisterResponse;
import com.example.safecamp.serviceImpl.RegistrationServiceImpl;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private RegisterRequest request;
    private User savedUser;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setName("Smit");
        request.setEmail("smit@test.com");
        request.setPhone("9999999999");
        request.setPassword("password123");
        request.setRole(Role.RESIDENT);

        savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setPhone(request.getPhone());
        savedUser.setRole(request.getRole());
        savedUser.setPassword("encoded-password");
    }

    // ---------- register ----------

    @Test
    void register_success() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegisterResponse response = registrationService.register(request);

        assertNotNull(response);
        assertEquals(savedUser.getId(), response.getUserId());
        assertEquals("Registration successful", response.getMessage());
        assertEquals(savedUser.getRole(), response.getRole());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailAlreadyRegistered_throwsException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> registrationService.register(request));

        assertEquals("Email already registered", ex.getMessage());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void register_invalidRole_throwsException() {
        request.setRole(Role.SECURITY); // not allowed

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> registrationService.register(request));

        assertEquals("Only Residents and guests can register", ex.getMessage());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}

