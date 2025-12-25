package com.example.safecamp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.dto.CreateEmergencyAlertRequest;
import com.example.safecamp.dto.EmergencyAlertResponse;
import com.example.safecamp.entity.EmergencyAlert;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.EmergencyStatus;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.EmergencyAlertRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.serviceImpl.EmergencyServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmergencyServiceImplTest {

    @Mock
    private EmergencyAlertRepository emergencyAlertRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmergencyServiceImpl emergencyService;

    private User resident;
    private User guard;

    @BeforeEach
    void setup() {
        resident = User.builder()
                .id(UUID.randomUUID())
                .name("RESIDENT One")
                .phone("9999999999")
                .role(Role.RESIDENT)
                .build();

        guard = User.builder()
                .id(UUID.randomUUID())
                .name("Guard One")
                .role(Role.SECURITY)
                .build();
    }

    @Test
    void createEmergency_success() {
        CreateEmergencyAlertRequest request = new CreateEmergencyAlertRequest(12.34, 56.78, "Help");

        when(userRepository.findById(resident.getId()))
                .thenReturn(Optional.of(resident));

        when(emergencyAlertRepository.existsByUserAndStatus(
                resident, EmergencyStatus.ACTIVE))
                .thenReturn(false);

        when(emergencyAlertRepository.save(any(EmergencyAlert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmergencyAlertResponse response = emergencyService.createEmergency(request, resident.getId());

        assertEquals(EmergencyStatus.ACTIVE, response.getStatus());
        assertEquals(resident.getId(), response.getUserId());
    }

    @Test
    void createEmergency_guardNotAllowed() {
        CreateEmergencyAlertRequest request = new CreateEmergencyAlertRequest(12.0, 45.0, null);

        when(userRepository.findById(guard.getId()))
                .thenReturn(Optional.of(guard));

        assertThrows(IllegalStateException.class, () -> emergencyService.createEmergency(request, guard.getId()));
    }

    @Test
    void createEmergency_duplicateActive() {
        CreateEmergencyAlertRequest request = new CreateEmergencyAlertRequest(1.0, 2.0, "Again");

        when(userRepository.findById(resident.getId()))
                .thenReturn(Optional.of(resident));

        when(emergencyAlertRepository.existsByUserAndStatus(
                resident, EmergencyStatus.ACTIVE))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> emergencyService.createEmergency(request, resident.getId()));
    }

    @Test
    void acknowledgeEmergency_success() {
        EmergencyAlert alert = EmergencyAlert.builder()
                .id(UUID.randomUUID())
                .user(resident)
                .status(EmergencyStatus.ACTIVE)
                .build();

        when(emergencyAlertRepository.findById(alert.getId()))
                .thenReturn(Optional.of(alert));

        when(userRepository.findById(guard.getId()))
                .thenReturn(Optional.of(guard));

        when(emergencyAlertRepository.save(alert))
                .thenReturn(alert);

        EmergencyAlertResponse response = emergencyService.acknowledgeEmergency(alert.getId(), guard.getId());

        assertEquals(EmergencyStatus.ACKNOWLEDGED, response.getStatus());
    }

    @Test
    void acknowledgeEmergency_nonGuardFails() {
        EmergencyAlert alert = EmergencyAlert.builder()
                .id(UUID.randomUUID())
                .user(resident)
                .status(EmergencyStatus.ACTIVE)
                .build();

        when(emergencyAlertRepository.findById(alert.getId()))
                .thenReturn(Optional.of(alert));

        when(userRepository.findById(resident.getId()))
                .thenReturn(Optional.of(resident));

        assertThrows(IllegalStateException.class,
                () -> emergencyService.acknowledgeEmergency(alert.getId(), resident.getId()));
    }

    @Test
    void resolveEmergency_success() {
        EmergencyAlert alert = EmergencyAlert.builder()
                .id(UUID.randomUUID())
                .user(resident)
                .status(EmergencyStatus.ACKNOWLEDGED)
                .build();

        when(emergencyAlertRepository.findById(alert.getId()))
                .thenReturn(Optional.of(alert));

        when(userRepository.findById(guard.getId()))
                .thenReturn(Optional.of(guard));

        when(emergencyAlertRepository.save(alert))
                .thenReturn(alert);

        EmergencyAlertResponse response = emergencyService.resolveEmergency(alert.getId(), guard.getId());

        assertEquals(EmergencyStatus.RESOLVED, response.getStatus());
        assertNotNull(alert.getResolvedAt());
    }

    @Test
    void resolveEmergency_alreadyResolved() {
        EmergencyAlert alert = EmergencyAlert.builder()
                .id(UUID.randomUUID())
                .status(EmergencyStatus.RESOLVED)
                .build();

        when(emergencyAlertRepository.findById(alert.getId()))
                .thenReturn(Optional.of(alert));

        assertThrows(IllegalStateException.class,
                () -> emergencyService.resolveEmergency(alert.getId(), guard.getId()));
    }

}