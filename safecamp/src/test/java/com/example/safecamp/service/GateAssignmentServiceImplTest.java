package com.example.safecamp.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.safecamp.entity.Gate;
import com.example.safecamp.entity.GateAssignment;
import com.example.safecamp.entity.User;
import com.example.safecamp.enums.Role;
import com.example.safecamp.repository.GateAssignmentRepository;
import com.example.safecamp.repository.GateRepository;
import com.example.safecamp.repository.UserRepository;
import com.example.safecamp.service.serviceImpl.GateAssignmentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GateAssignmentServiceImplTest {

    @Mock
    private GateAssignmentRepository gateAssignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GateRepository gateRepository;

    @InjectMocks
    private GateAssignmentServiceImpl gateAssignmentService;

    private User admin;
    private User guard;
    private Gate gate;

    @BeforeEach
    void setup() {
        admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(Role.ADMIN);
        admin.setName("Admin");

        guard = new User();
        guard.setId(UUID.randomUUID());
        guard.setRole(Role.SECURITY);
        guard.setName("Guard A");

        gate = new Gate();
        gate.setId(UUID.randomUUID());
        gate.setName("Main Gate");
    }

    @Test
    void shouldRejectAssignment_whenUserIsNotAdmin() {

        User resident = new User();
        resident.setId(UUID.randomUUID());
        resident.setRole(Role.RESIDENT);

        when(userRepository.findById(resident.getId()))
                .thenReturn(Optional.of(resident));

        assertThatThrownBy(() -> gateAssignmentService.assignGuardToGate(
                guard.getId(),
                gate.getId(),
                LocalDateTime.now(),
                null,
                resident.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only admin can assign guards to gates");
    }

    @Test
    void shouldRejectAssignment_whenUserIsNotSecurity() {

        User staff = new User();
        staff.setId(UUID.randomUUID());
        staff.setRole(Role.STAFF);

        when(userRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));

        when(userRepository.findById(staff.getId()))
                .thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> gateAssignmentService.assignGuardToGate(
                staff.getId(),
                gate.getId(),
                LocalDateTime.now(),
                null,
                admin.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User is not a security guard");
    }

    @Test
    void shouldRejectAssignment_whenEndTimeBeforeStartTime() {

        when(userRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));

        when(userRepository.findById(guard.getId()))
                .thenReturn(Optional.of(guard));

        when(gateRepository.findById(gate.getId()))
                .thenReturn(Optional.of(gate));

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusHours(1);

        assertThatThrownBy(() -> gateAssignmentService.assignGuardToGate(
                guard.getId(),
                gate.getId(),
                start,
                end,
                admin.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("End time cannot be before start time");
    }

    @Test
    void shouldEndAssignment_whenAdminRequests() {

        GateAssignment assignment = new GateAssignment();
        assignment.setId(UUID.randomUUID());
        assignment.setGuard(guard);
        assignment.setGate(gate);
        assignment.setStartTime(LocalDateTime.now().minusHours(2));

        when(userRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));

        when(gateAssignmentRepository.findById(assignment.getId()))
                .thenReturn(Optional.of(assignment));

        gateAssignmentService.endAssignment(
                assignment.getId(),
                admin.getId());

        assertThat(assignment.getEndTime()).isNotNull();
        verify(gateAssignmentRepository).save(assignment);
    }

}
